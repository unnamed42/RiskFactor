const { join, resolve } = require("path");

const HtmlWebpackPlugin = require("html-webpack-plugin");
const { CleanWebpackPlugin } = require("clean-webpack-plugin");
const FriendlyErrorsWebpackPlugin = require("@soda/friendly-errors-webpack-plugin")
const MiniCssExtractPlugin = require("mini-css-extract-plugin");
const ForkTsCheckerWebpackPlugin = require('fork-ts-checker-webpack-plugin');

const root = resolve("./");

module.exports = {
  stats: {
    modules: false,
    reasons: false,
    chunks: false,
    chunkModules: false,
    hash: false,
    children: false,
  },
  entry: join(root, "/src/index.tsx"),
  output: {
    path: join(root, "/dist"),
    filename: "static/[name].[fullhash:5].js",
    chunkFilename: "static/[name].[fullhash:5].js",
    publicPath: ""
  },
  resolve: {
    extensions: [".ts", ".tsx", ".js"],
    alias: {
      "@": join(root, "/src"),
      "@public": join(root, "/public")
    }
  },
  module: {
    rules: [
      {
        test: /\.[tj]sx?$/,
        exclude: /node_modules/,
        loader: require.resolve("babel-loader"),
        options: { cacheDirectory: true },
      },
      {
        test: /\.(le|c)ss$/,
        use: [
          {
            loader: MiniCssExtractPlugin.loader,
            options: {
              esModule: true
            },
          },
          {
            loader: require.resolve("css-loader"),
            options: {
              modules: {
                auto: /\.mod\.(le|c)ss$/i,
                exportLocalsConvention: "camelCaseOnly",
              },
              importLoaders: 2,
              esModule: true
            }
          },
          require.resolve("postcss-loader"),
          {
            loader: require.resolve("less-loader"),
            options: {
              lessOptions: {
                javascriptEnabled: true
              }
            }
          }
        ]
      },
      {
        test: /\.(png|svg|jpe?g|gif|woff2?|eot|ttf|otf)$/,
        loader: require.resolve("file-loader"),
        options: {
          outputPath: "assets"
        }
      }
    ]
  },
  performance: {
    hints: process.env.NODE_ENV === "production" ? "warning" : false,
  },
  plugins: [
    new FriendlyErrorsWebpackPlugin(),
    new CleanWebpackPlugin(),
    new HtmlWebpackPlugin({
      filename: "index.html",
      template: join(root, "/src/index.html"),
      minify: true
    }),
    new MiniCssExtractPlugin({
      filename: "static/[name].[fullhash:5].css",
      chunkFilename: "static/[name].[fullhash:5].css",
      ignoreOrder: true,
    }),
    new ForkTsCheckerWebpackPlugin({
      typescript: {
        diagnosticOptions: {
          semantic: true,
          syntactic: true,
        },
        mode: "write-references",
      },
      eslint: {
        enabled: true,
        files: "./src/**/*.{ts,tsx}"
      }
    })
  ],
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
