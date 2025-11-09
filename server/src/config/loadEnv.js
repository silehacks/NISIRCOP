import dotenv from 'dotenv';

let cachedEnv = null;

export function loadEnv() {
  if (cachedEnv) {
    return cachedEnv;
  }

  const { parsed } = dotenv.config();
  cachedEnv = {
    NODE_ENV: process.env.NODE_ENV || 'development',
    PORT: process.env.PORT || parsed?.PORT,
    CLIENT_URL: process.env.CLIENT_URL || parsed?.CLIENT_URL,
    JWT_SECRET: process.env.JWT_SECRET || parsed?.JWT_SECRET || 'super-secret-key',
    MONGO_URI: process.env.MONGO_URI || parsed?.MONGO_URI || 'mongodb://localhost:27017/nisircop',
    SOCKET_PATH: process.env.SOCKET_PATH || parsed?.SOCKET_PATH
  };

  return cachedEnv;
}
