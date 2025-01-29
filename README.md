# Express - Pug - Sass - TypeScript - Vitest Scaffold 2025

it's basically on the tin.

## a note on styles

maybe this is unconventional, but I have sass files in `/src/sass` which are compiled by `express-dart-sass` middleware into `/src/css`. All files in `/src/css/` are ignored by git. Keep the `css` folder empty, and its contents will be generated as necessary. All other public files go in `/src/assets`.
