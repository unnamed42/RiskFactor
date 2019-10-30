import React, { FC } from "react";
import { Switch } from "react-router-dom";

import { PrivateRoute } from "@/components";
import { TaskList } from "./TaskList";
import { Answers } from "./Answers";

export const Routes: FC = () =>
  <Switch>
    <PrivateRoute exact path="/" component={TaskList} />
    <PrivateRoute path="/task/:id"
      render={({ match }) => <Answers taskId={match.params.id} />} />
  </Switch>;
