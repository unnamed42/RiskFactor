const merge = require("webpack-merge");
const common = require("./webpack.base");
// const WebpackCdnPlugin = require("webpack-cdn-plugin");
const { BundleAnalyzerPlugin } = require("webpack-bundle-analyzer");
const TerserPlugin = require("terser-webpack-plugin");
const CopyPlugin = require("copy-webpack-plugin");

module.exports = merge(common, {
  plugins: [
    new CopyPlugin([
      { from: "public", to: "public" }
    ]),
    // new WebpackCdnPlugin({
    //   modules: [{
    //     name: "react-dom",
    //     var: "ReactDOM",
    //     path: "cjs/react-dom.production.min.js"
    //   }]
    // }),
    new BundleAnalyzerPlugin()
  ],
  optimization: {
    minimize: true,
    minimizer: [new TerserPlugin({
      terserOptions: {
        compress: {
          drop_console: true
        }
      }
    })]
  }
});
