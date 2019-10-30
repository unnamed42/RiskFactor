import React, { FC, useState, useEffect, useRef } from "react";

import {message, Layout, Menu, Icon, PageHeader} from "antd";

import {PageLoading} from "@/components";
import { QForm, QFormD } from "./QForm";

import { Section } from "@/types/task";
import {answer, postAnswer, taskSections} from "@/api/task";

interface P {
  taskId: number | string;
  answerId?: number | string;
  sectionId?: number | string;
}

interface S {
  sections?: Section[];
  sectionPath?: string;
  opened: string[];
  answers: { [k: string]: any };
}

export const AnswerForm: FC<P> = ({ taskId, answerId, sectionId }) => {

  const [state, setState] = useState<S>({ sectionPath: `${sectionId || 0}/0`, answers: {}, opened: ["0"] });
  const form = useRef<QFormD>(null);

  useEffect(() => {
    Promise.all([taskSections(taskId), answerId ? answer(answerId) : Promise.resolve({})])
        .then(([secs, ans]) => setState({ ...state, sections: secs, answers: ans }))
        .catch(err => message.error(err.message));
  }, []);

  const selected = (parent: number, child: number) => {
    const nextState = { ...state, sectionPath: `${parent}/${child}` };
    if(!opened.includes(parent.toString()))
      nextState.opened.push(parent.toString());
    setState(nextState);
  };

  const saveLocal = () => {
    if(!form.current)
      return;
    return form.current.submit().then(values => {
      let pIdx = Number(parent), cIdx = Number(child);
      answers[`${sections[pIdx].id}/${sections[pIdx].sections![cIdx].id}`] = values;
      if(cIdx + 1 !== sections[pIdx].sections!.length)
        ++cIdx;
      else if(pIdx + 1 !== sections.length) {
        ++pIdx; cIdx = 0;
      }
      selected(pIdx, cIdx);
      return values;
    });
  };

  const saveRemote = () => {
    const promise = saveLocal();
    if(!promise) return;
    return promise.then(values => postAnswer(taskId, answers), err => message.error(err.message))
        .then(() => message.success("表单数据提交成功"));
  };

  if(!state || !state.sections || !state.sectionPath)
    return <PageLoading />;

  const { sectionPath, sections, opened, answers } = state;
  const [parent, child] = sectionPath.split("/");
  const sectionIdPath = `${sections[Number(parent)].id}/${sections[Number(parent)].sections![Number(child)].id}`;

  return <Layout>
    <Layout.Sider style={{ width: 200, background: "#fff" }}>
      <Menu mode="inline" selectedKeys={[sectionPath]} defaultOpenKeys={opened}
        style={{ height: "100%", borderRight: 0 }}
      >
        {
          sections.map(({ id, sections, title }, idx) =>
            <Menu.SubMenu key={idx} title={<span><Icon type="bars" />{title}</span>}>
              {
                sections && sections.map((sec, ndx) =>
                  <Menu.Item key={`${idx}/${ndx}`} onClick={() => selected(idx, ndx)}>{sec.title}</Menu.Item>
                )
              }
            </Menu.SubMenu>
          )
        }
      </Menu>
    </Layout.Sider>
    <Layout>
      <Layout.Content style={{ padding: "20px 24px", minHeight: 280 }}>
        <PageHeader title=" " onBack={() => window.location.hash = `/task/${taskId}/answers`}/>
        <QForm source={sections[Number(parent)].sections![Number(child)]}
          answer={answers[sectionIdPath]}
          wrappedComponentRef={form} onSave={saveLocal} onSubmit={saveRemote}
        />
      </Layout.Content>
    </Layout>
  </Layout>;

};

export default AnswerForm;
