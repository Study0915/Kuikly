const http = require('node:http');
const fs = require('node:fs');
const path = require('node:path');
const root = path.resolve(__dirname, '..');
const port = Number(process.argv[2] || 18761);
if (!Number.isInteger(port) || port < 1024 || port > 65535) throw new Error('Invalid local port');
const files = {
  '/': ['h5App/src/jsMain/resources/index.html', 'text/html; charset=utf-8'],
  '/demo-frame.html': ['scripts/demo-frame.html', 'text/html; charset=utf-8'],
  '/h5App.js': ['h5App/build/kotlin-webpack/js/productionExecutable/h5App.js', 'text/javascript; charset=utf-8'],
  '/h5App.js.map': ['h5App/build/kotlin-webpack/js/productionExecutable/h5App.js.map', 'application/json'],
};
if (!fs.existsSync(path.join(root, files['/h5App.js'][0]))) throw new Error('Build the H5 production bundle first');
http.createServer((req, res) => {
  const pathname = new URL(req.url, 'http://127.0.0.1').pathname;
  if (pathname === '/favicon.ico') { res.writeHead(204); res.end(); return; }
  const file = files[pathname];
  if (!file) { res.writeHead(404); res.end(); return; }
  res.setHeader('Cache-Control', 'no-store');
  res.setHeader('Content-Type', file[1]);
  const stream = fs.createReadStream(path.join(root, file[0]));
  stream.on('error', () => { if (!res.headersSent) res.writeHead(404); res.end(); });
  stream.pipe(res);
}).listen(port, '127.0.0.1', () => console.log(`TASK1_H5_URL=http://127.0.0.1:${port}`));
