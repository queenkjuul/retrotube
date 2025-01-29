import express from "express"
import sassMiddleware from "express-dart-sass"
import morgan from "morgan"
import path from "path"
import { fileURLToPath } from "url"

const __filename = fileURLToPath(import.meta.url)
const __dirname = path.dirname(__filename)

const app = express()
const port = "3000"

app.set("views", path.join(__dirname, "views"))
app.set("view engine", "pug")

if (process.env.DEBUG_LOG) {
  app.use(morgan("tiny"))
}

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

app.get("/", (req, res) => {
  res.render("index")
  console.log("Response sent")
})

app.listen(port, () => {
  console.log(`Example app listening on port ${port}`)
})
