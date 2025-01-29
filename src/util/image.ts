import { resizeImage } from "./reize-image-buffer.js"

export async function getImageFile(
  url,
  width: string | number = 200,
  height: string | number = 150
) {
  try {
    if (!url) return
    if (typeof width === "string") {
      width = parseInt(width)
    }
    if (typeof height === "string") {
      height = parseInt(height)
    }
    const response = await fetch(url)
    const imageData = await response.arrayBuffer()
    const imageBuffer = Buffer.from(imageData)
    const image = await resizeImage(imageBuffer, { width, height })
    return image
  } catch (e) {
    console.error(e)
  }
}
