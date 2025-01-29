import { execSync, spawn } from "child_process"

export function startStream(url) {
  const binPath = execSync("which vlc").toString().replace("\n", "")
  const vlc = spawn(binPath, [
    url,
    "--network-caching=1000",
    "--sout",
    "#transcode{vcodec=WMV2,vb=300,scale=0.5,fps=15,acodec=wma,ab=32,channels=2}:std{access=mmsh,mux=asfh,dst=:8080}",
    "-I",
    "dummy",
  ])
  vlc.stderr.on("error", (d) => console.log(d))
  vlc.stdout.on("data", (d) => console.log(d.toString()))
  vlc.stdout.on("close", (c) => console.log("process exit: " + c))
}
