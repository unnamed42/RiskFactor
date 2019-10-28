import React, { FC, useState, useEffect } from "react";

import { Steps, Button } from "antd";

import { QForm } from "./QForm";
import { getSections } from "@/api/forms";

interface S {
  curr: number;
  data: Section[];
}

export const Forms: FC = () => {

  const [state, setState] = useState<S>();

  const loadForms = () =>
    getSections().then(({ sections }) => setState({ curr: 0, data: sections }));

  useEffect(() => { loadForms(); }, []);

  const prev = () => setState({ ...state!, curr: state!.curr - 1 });
  const next = () => setState({ ...state!, curr: state!.curr + 1 });

  return <div>
    <Steps current={state && state.curr} style={{marginBottom: "20px"}}>
      {
        state && state.data.map(({ title }) =>
          <Steps.Step key={title} title={title}/>
        )
      }
    </Steps>
    <QForm source={state && state.data[state.curr]} />
    <div className="step-navigation">
      {
        state && state.curr < state.data.length - 1 && (
          <Button type="primary" onClick={next}>
            下一步
          </Button>
        )
      }
      {
        state && state.curr === state.data.length - 1 && (
          <Button type="primary" onClick={() => console.log("done")}>
            完成
          </Button>
        )
      }
      {
        state && state.curr > 0 && (
          <Button style={{ marginLeft: 8 }} onClick={prev}>
            上一步
          </Button>
        )
      }
    </div>
  </div>;
};

export default Forms;
