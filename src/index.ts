import bodyParser from "body-parser"
import express from "express"
import sassMiddleware from "express-dart-sass"
import morgan from "morgan"
import path from "path"
import { Stream } from "stream"
import { setTimeout } from "timers/promises"
import { fileURLToPath } from "url"
import { getImageFile } from "./util/image.js"
import { startStream } from "./vlcClient.js"
import { exec } from "./ytclient.js"

export const __filename = fileURLToPath(import.meta.url)
export const __dirname = path.dirname(__filename)

const app = express()
const port = "3000"

app.set("views", path.join(__dirname, "views"))
app.set("view engine", "pug")

if (process.env.DEBUG_LOG) {
  app.use(morgan("tiny"))
}

app.use(bodyParser.urlencoded())

app.use(
  sassMiddleware({
    /* Options */
    src: path.join(__dirname, "sass"),
    dest: path.join(__dirname, "css"),
    debug: process.env.DEBUG_LOG ? true : false,
    outputStyle: "compressed",
    prefix: "/css",
  })
)
app.use("/css", express.static(path.join(__dirname, "css")))
app.use(
  "/favicon.ico",
  express.static(path.join(__dirname, "assets/favicon.ico"))
)
app.use("/assets", express.static(path.join(__dirname, "assets")))

app.use("/image", async (req, res) => {
  const url = req.query.url
  const width = req.query.width
  const height = req.query.height
  try {
    const data = await getImageFile(
      url?.toString(),
      width?.toString(),
      height?.toString()
    )
    const readStream = new Stream.PassThrough()
    readStream.end(data)
    res.set("Content-disposition", "attachment; filename=albumart.jpg")
    res.set("Content-Type", "image/jpeg")
    readStream.pipe(res)
  } catch (e) {
    console.error(e)
    res.status(400)
    res.send(e)
  }
})

app.get("/video", async (req, res) => {
  const url = req.query.url
  const videoJson = exec([url]).toString()
  const videoData = JSON.parse(videoJson)
  // currently unused (but might be in future) - delete for easier debugging
  // delete videoData.relatedItems
  // delete videoData.subtitles
  // delete videoData.audioStreams
  // delete videoData.videoOnlyStreams
  const stream = videoData.videoStreams[0]
  startStream(stream.content)
  await setTimeout(3000)
  res.render("video", {
    video: videoData,
    title: videoData.name,
    width: stream.width.toString(),
    height: stream.height.toString(),
  })
})

app.get("/embed/stream.asx", (req, res) => {
  const title = req.query.title || "RetroTube"
  res.send(`
    <ASX version ="3.0">
    <TITLE>${title}</TITLE>
      <ENTRY>
        <REF HREF="mms://10.69.69.13:8080" />
      </ENTRY>
    </ASX>
`)
})

app.get("/", (_req, res) => {
  const homepageJson = exec(["trending"]).toString()
  const homepageData = JSON.parse(homepageJson)
  res.render("index", { data: homepageData, title: "RetroTube" })
})

app.post("/search", (req, res) => {
  const searchResultsJson = exec(["search", `'${req.body.query}'`]).toString()
  const searchResultsData = JSON.parse(searchResultsJson)
  res.render("index", {
    data: searchResultsData,
    title: `Search Results for ${req.body.query}`,
  })
})

app.listen(port, () => {
  console.log(`Example app listening on port ${port}`)
})
