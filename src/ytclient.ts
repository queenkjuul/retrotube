import { execSync } from "child_process"
import path from "path"
import { fileURLToPath } from "url"

const __filename = fileURLToPath(import.meta.url)
const __dirname = path.dirname(__filename)

const binPath = path.join(__dirname, "ytclient/app/build/install/app/bin/app")

export function exec(commands) {
  commands = binPath + " " + commands.join(" ")
  // There is a limit at 200KB (increase it with maxBuffer option)
  const stdout = execSync(commands)
  return stdout
}
