const { merge } = require("webpack-merge");
const common = require("./webpack.base");
const { resolve } = require("path")

module.exports = merge(common, {
  mode: "development",
  devtool: "eval-cheap-module-source-map",
  devServer: {
    static: [
      resolve(__dirname, "dist")
    ],
    host: "0.0.0.0",
    firewall: false,
  }
});
