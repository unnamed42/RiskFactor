const { join } = require("path");
const merge = require("webpack-merge");
const { BundleAnalyzerPlugin } = require("webpack-bundle-analyzer");
const HtmlWebpackPlugin = require("html-webpack-plugin");
const { CleanWebpackPlugin } = require("clean-webpack-plugin");
const FriendlyErrorsWebpackPlugin = require("friendly-errors-webpack-plugin");

const root = join(__dirname, "../..");

const config = {
  entry: [`${root}/src/index`],
  output: {
    path: `${root}/dist`,
    filename: "[name].[hash:5].js",
    chunkFilename: "[name].[hash:5].js",
    publicPath: "/"
  },
  resolve: {
    extensions: [".ts", ".tsx", ".js", ".jsx"],
    alias: {
      "@": `${root}/src`
    }
  },
  plugins: [
    new CleanWebpackPlugin(),
    new HtmlWebpackPlugin({
      template: `${root}/src/index.html`
    }),
    new FriendlyErrorsWebpackPlugin(),
    new BundleAnalyzerPlugin()
  ],
  module: {
    rules: [{
      test: /\.[tj]sx?$/,
      exclude: /node_modules/,
      use: ["babel-loader"]
    }, {
      enforce: "pre",
      test: /\.js$/,
      loader: "source-map-loader"
    }]
  },
  optimization: {
    splitChunks: {
      chunks: chunk => chunk.name !== "antd-icons",
      cacheGroups: {
        vendor: {
          test: /node_modules/,
          chunks: "initial",
          name: "vendor",
          enforce: true
        }
      }
    }
  }
};

module.exports = [ /* "antd", */ "env", "style", "moment"].reduce((config, name) => {
  const partialConfig = require(`./${name}`);
  return merge(config, partialConfig);
}, config);