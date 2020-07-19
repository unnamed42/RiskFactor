// eslint-disable-next-line @typescript-eslint/no-unused-vars
import React from "react";
import dayjs from "dayjs";
import customParseFormat from "dayjs/plugin/customParseFormat";
import "dayjs/locale/zh-cn";

dayjs.extend(customParseFormat);

dayjs.locale("zh-cn");

// if (process.env.NODE_ENV === "development") {
//   // eslint-disable-next-line @typescript-eslint/no-var-requires
//   const whyDidYouRender = require("@welldone-software/why-did-you-render");
//   whyDidYouRender(React, {
//     trackAllPureComponents: true,
//     trackExtraHooks: [
//       [require("react-redux/lib"), "useSelector"]
//     ]
//   });
// }
