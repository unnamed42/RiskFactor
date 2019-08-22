const { join, resolve } = require("path");
const { existsSync } = require("fs");
const { config } = require("dotenv");
const { BundleAnalyzerPlugin } = require("webpack-bundle-analyzer");
const HtmlWebpackPlugin = require("html-webpack-plugin");
const { ContextReplacementPlugin, DefinePlugin } = require("webpack");
const MiniCssExtractPlugin = require("mini-css-extract-plugin");
const OptimizeCSSAssetsPlugin = require("optimize-css-assets-webpack-plugin");
const { CleanWebpackPlugin } = require("clean-webpack-plugin");

function envKeys() {
  const defaultEnv = join(__dirname, "/.env");
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
  entry: ["./src/index"],
  output: {
    path: join(__dirname, "/dist"),
    filename: "[name].[hash:5].js",
    chunkFilename: "[name].[hash:5].js",
    publicPath: "/"
  },
  resolve: {
    extensions: [".ts", ".tsx", ".js", ".jsx"],
    alias: {
      "@": resolve("src")
    }
  },
  plugins: [
    new CleanWebpackPlugin(),
    new DefinePlugin(envKeys(process.env)),
    // remove unneeded locales from moment.js
    new ContextReplacementPlugin(/moment[\/\\]locale$/, /zh-cn/),
    new HtmlWebpackPlugin({
      template: "./src/index.html"
    }),
    new MiniCssExtractPlugin({
      filename: "[name].[hash:5].css",
      chunkFilename: "[name].[hash:5].css",
      // ignoreOrder: false,
    }),
    new BundleAnalyzerPlugin()
  ],
  module: {
    rules: [{
      test: /\.[tj]sx?$/,
      exclude: /node_modules/,
      use: ["babel-loader"]
    }, {
      test: /\.(le|c)ss$/,
      use: [
        {
          loader: MiniCssExtractPlugin.loader,
          options: {
            hmr: process.env.NODE_ENV === "development"
          },
        },
        "css-loader",
        {
          loader: "less-loader",
          options: { javascriptEnabled: true }
        }
      ]
    }, {
      enforce: "pre",
      test: /\.js$/,
      loader: "source-map-loader"
    }, {
      enforce: "pre",
      include: [require.resolve("@ant-design/icons/lib/dist")],
      loader: "webpack-ant-icon-loader"
    }]
  },
  optimization: {
    minimizer: [
      new OptimizeCSSAssetsPlugin({})
    ],
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
