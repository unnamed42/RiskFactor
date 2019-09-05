import React, { FC } from "react";

const App: FC = () => (
  <div>
    <h1>Hello, world!</h1>
    <p>Our API base url is <a href={process.env.API_BASE}>{process.env.API_BASE}</a></p>
  </div>
);

export default App;
