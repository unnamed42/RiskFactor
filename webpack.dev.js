const merge = require("webpack-merge");
const common = require("./scripts/webpack");

module.exports = merge(common, {
  devtool: "cheap-module-eval-source-map",
  devServer: {
    contentBase: "./",
    hot: true
  }
});
