import React, { FC } from "react";

import { LoginForm } from "@/components";

const App: FC = () => (
  <div>
    <h1>Hello, world!</h1>
    <p>Our API base url is <a href={process.env.API_BASE}>{process.env.API_BASE}</a></p>
    <LoginForm/>
  </div>
);

export default App;
