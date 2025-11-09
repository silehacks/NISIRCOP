const levels = ['debug', 'info', 'warn', 'error'];

function log(level, scope, message, meta) {
  const timestamp = new Date().toISOString();
  const payload = meta && Object.keys(meta).length > 0 ? ` ${JSON.stringify(meta)}` : '';
  // eslint-disable-next-line no-console
  console[level === 'error' ? 'error' : 'log'](`[${timestamp}] [${scope}] ${level.toUpperCase()}: ${message}${payload}`);
}

export function createLogger(scope = 'app') {
  return levels.reduce((acc, level) => {
    acc[level] = (message, meta = {}) => log(level, scope, message, meta);
    return acc;
  }, {});
}
