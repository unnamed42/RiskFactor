import React, { FC, useState, useEffect, useRef } from "react";
import { withRouter, RouteComponentProps } from "react-router";

import { Steps, Button } from "antd";

import { QForm, QFormD } from "./QForm";
import { PageLoading } from "@/components";
import * as api from "@/api/forms";

interface S {
  curr: number;
  data: Section[];

  [page: number]: any;
}

interface ParamType {
  title: string;
}

type P = RouteComponentProps<ParamType>;

export const Forms = withRouter<P, FC<P>>(({ match }) => {

  const [state, setState] = useState<S>();
  const form = useRef<QFormD>(null);

  const { title } = match.params;

  const navigate = (diff: number) =>
    ({ ...state!, curr: state!.curr + diff });

  const page = (diff: number) => {
    const { current } = form;
    if (current == null)
      return;
    current.submit().then(values => {
      if (!state)
        return;
      setState({
        ...navigate(diff),
        [state!.curr]: values
      });
    }, _ => { });
  };

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
    <QForm source={state.data[state.curr]}
      answer={state[state.curr]}
      wrappedComponentRef={form} />
    <div className="step-navigation">
      {
        state.curr < state.data.length - 1 && (
          <Button type="primary" onClick={() => page(1)}>
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
          <Button style={{ marginLeft: 8 }} onClick={() => setState(navigate(-1))}>
            上一步
          </Button>
        )
      }
    </div>
  </div>;
});

export default Forms;
