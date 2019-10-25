const merge = require("webpack-merge");
const HtmlWebpackPlugin = require("html-webpack-plugin");
const { CleanWebpackPlugin } = require("clean-webpack-plugin");
const FriendlyErrorsWebpackPlugin = require("friendly-errors-webpack-plugin");

const root = require("./root");

const config = {
  stats: { children: false },
  entry: [`${root}/src/index`],
  output: {
    path: `${root}/dist`,
    filename: "[name].js",
    chunkFilename: "[name].js",
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
    new FriendlyErrorsWebpackPlugin()
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
      cacheGroups: {
        default: false,
        vendor: {
          name: "vendor",
          chunks: "initial",
          test: /node_modules/,
          priority: 20
        },
        common: {
          name: "common",
          minChunks: 2,
          chunks: "async",
          priority: 10,
          reuseExistingChunk: true,
          // enforce: true
        }
      }
    }
  }
};

const enabled = [
  "antd",
  "style",
  "moment",
  "env"
];

module.exports = enabled.reduce((config, name) =>
  merge(config, require(`./${name}`))
, config);
