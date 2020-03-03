const { join, resolve } = require("path");

const HtmlWebpackPlugin = require("html-webpack-plugin");
const { CleanWebpackPlugin } = require("clean-webpack-plugin");
const FriendlyErrorsWebpackPlugin = require("friendly-errors-webpack-plugin");
const MiniCssExtractPlugin = require("mini-css-extract-plugin");

const root = resolve("./");

module.exports = {
  mode: process.env.NODE_ENV,
  stats: {children: false},
  entry: join(root, "/src/index.tsx"),
  output: {
    path: join(root, "/dist/static"),
    filename: "[name].[hash:5].js",
    chunkFilename: "[name].[hash:5].js",
    publicPath: "/static/"
  },
  resolve: {
    extensions: [".ts", ".tsx", ".js"],
    alias: {
      "@": join(root, "/src")
    }
  },
  module: {
    rules: [
      {
        test: /\.tsx?$/,
        exclude: /node_modules/,
        loader: "babel-loader",
        options: { cacheDirectory: true },
      },
      {
        test: /\.(le|c)ss$/,
        use: [
          {
            loader: MiniCssExtractPlugin.loader,
            options: {
              hmr: process.env.NODE_ENV === "development",
              esModule: true
            },
          },
          "css-loader",
          {
            loader: "less-loader",
            options: { javascriptEnabled: true }
          }
        ]
      },
      {
        test: /\.(png|svg|jpe?g|gif|woff2?|eot|ttf|otf)$/,
        use: "file-loader"
      }
    ]
  },
  performance: {
    hints: process.env.NODE_ENV === "production" ? "warning" : false,
  },
  plugins: [
    new CleanWebpackPlugin(),
    new HtmlWebpackPlugin({
      filename: join(root, "/dist/index.html"),
      template: join(root, "/src/index.html")
    }),
    new FriendlyErrorsWebpackPlugin(),
    new MiniCssExtractPlugin({
      filename: "[name].[hash:5].css",
      chunkFilename: "[name].[hash:5].css",
      ignoreOrder: true,
    }),
  ],
  optimization: {
    // runtimeChunk: "single",
    moduleIds: "hashed",
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
          enforce: true
        }
      }
    }
  }
};

// function envKeys() {
//     const defaultEnv = join(__dirname, "../../.env");
//     const envFile = `${defaultEnv}.${process.env.NODE_ENV}`;
//     // fallback to .env if .env.production | .env.development not found
//     const envPath = existsSync(envFile) ? envFile :
//         existsSync(defaultEnv) ? defaultEnv : null;
//     const keys = envPath ? config({ path: envPath }).parsed : {};
//     return Object.keys(keys).reduce((obj, key) => {
//         obj[`process.env.${key}`] = JSON.stringify(keys[key]);
//         return obj;
//     }, {});
// }
//
// module.exports = {
//     plugins: [
//         new DefinePlugin(envKeys())
//     ]
// };
