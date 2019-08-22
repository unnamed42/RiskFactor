const { join } = require("path");
const { existsSync } = require("fs");
const { config } = require("dotenv");
const { DefinePlugin } = require("webpack");

function envKeys() {
  const defaultEnv = join(__dirname, "../../.env");
  const envFile = `${defaultEnv}.${process.env.NODE_ENV}`;
  const keys = config({
    // fallback to .env if .env.production | .env.development not found
    path: existsSync(envFile) ? envFile : defaultEnv
  }).parsed;
  return Object.keys(keys).reduce((obj, key) => {
    obj[`process.env.${key}`] = JSON.stringify(keys[key]);
    return obj;
  }, {});
};

module.exports = {
  plugins: [
    new DefinePlugin(envKeys())
  ]
};