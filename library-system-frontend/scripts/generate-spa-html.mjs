import { promises as fs } from 'node:fs'
import path from 'node:path'

const clientDir = path.resolve('dist/client')
const assetsDir = path.join(clientDir, 'assets')

async function main() {
  const files = await fs.readdir(assetsDir)
  const jsCandidates = files.filter((f) => /^index-.*\.js$/.test(f))
  if (jsCandidates.length === 0) {
    throw new Error('No index-*.js file found in dist/client/assets')
  }

  const stats = await Promise.all(
    jsCandidates.map(async (f) => ({ f, s: await fs.stat(path.join(assetsDir, f)) }))
  )
  stats.sort((a, b) => b.s.size - a.s.size)
  const entryJs = stats[0].f

  const cssCandidates = files.filter((f) => /^styles-.*\.css$/.test(f))
  const cssLink = cssCandidates[0] ? `<link rel="stylesheet" href="/assets/${cssCandidates[0]}">` : ''

  const html = `<!doctype html>
<html lang="zh-Hant">
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Library Borrowing System</title>
    ${cssLink}
  </head>
  <body>
    <div id="root"></div>
    <script type="module" src="/assets/${entryJs}"></script>
  </body>
</html>
`

  await fs.writeFile(path.join(clientDir, 'index.html'), html, 'utf8')
  await fs.copyFile(path.join(clientDir, 'index.html'), path.join(clientDir, '404.html'))
  console.log(`Generated dist/client/index.html using ${entryJs}`)
}

main().catch((err) => {
  console.error(err)
  process.exit(1)
})
