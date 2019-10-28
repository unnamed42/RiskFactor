import React, { FC, useState, useEffect } from "react";
import { withRouter, RouteComponentProps } from "react-router";

import { Steps, Button, Checkbox, Input } from "antd";

import { QForm } from "./QForm";
import { PageLoading } from "@/components";
import * as api from "@/api/forms";

interface S {
  curr: number;
  data: Section[];
}

interface ParamType {
  title: string;
}

type P = RouteComponentProps<ParamType>;

export const Forms = withRouter<P, FC<P>>(({ match }) => {

  const [state, setState] = useState<S>();

  const { title } = match.params;

  const prev = () => setState({ ...state!, curr: state!.curr - 1 });
  const next = () => setState({ ...state!, curr: state!.curr + 1 });

  useEffect(() => {
    api.sectionsByName({ name: title }).then(({ sections }) =>
      setState({ curr: 0, data: sections })
    );
  }, []);

  if (!title || !state || !state.data)
    return <PageLoading />;

  return <div>
    <Steps current={state.curr} style={{marginBottom: "20px"}}>
      {
        state.data.map(({ title }) =>
          <Steps.Step key={title} title={title}/>
        )
      }
    </Steps>
    <QForm source={state.data[state.curr]} />
    <div className="step-navigation">
      {
        state.curr < state.data.length - 1 && (
          <Button type="primary" onClick={next}>
            下一步
          </Button>
        )
      }
      {
        state.curr === state.data.length - 1 && (
          <Button type="primary" onClick={() => console.log("done")}>
            完成
          </Button>
        )
      }
      {
        state.curr > 0 && (
          <Button style={{ marginLeft: 8 }} onClick={prev}>
            上一步
          </Button>
        )
      }
    </div>
  </div>;
});

export default Forms;
