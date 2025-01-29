// modfiied from https://github.com/velusgautam/resize-image-buffer/blob/master/index.js
// TODO: Fork, publish version using newer dependencies and TypeScript
import Jimp from "jimp"

type _resizeOptions = {
  width?: number
  height?: number
  format?: SupportedFormats
}
type ResizeOptions = RequireAtLeastOne<_resizeOptions, "width" | "height">

type SupportedFormats =
  | typeof Jimp.MIME_BMP
  | typeof Jimp.MIME_JPEG
  | typeof Jimp.MIME_PNG

export const resizeImage = async (
  buffer: Buffer,
  options: Partial<ResizeOptions> = {}
) => {
  const image = await Jimp.read(buffer)
  const mime = options.format || Jimp.MIME_JPEG

  if (typeof options.width !== "number") {
    options.width = Math.trunc(
      image.bitmap.width * (options.height / image.bitmap.height)
    )
  }

  if (typeof options.height !== "number") {
    options.height = Math.trunc(
      image.bitmap.height * (options.width / image.bitmap.width)
    )
  }

  return image.resize(options.width, options.height).getBufferAsync(mime)
}
