import React, { FC, useState, useEffect, useRef } from "react";

import { message, Layout, Menu, Icon, PageHeader } from "antd";

import { PageLoading } from "@/components";
import { QForm, QFormD } from "./QForm";

import { answer, postAnswer, taskSections } from "@/api/task";
import { usePromise } from "@/utils";

interface P {
  taskId: number | string;
  answerId?: number | string;
  sectionId?: number | string;
}

interface S {
  parent: number;
  child: number;
  opened: string[];
  answers: any;
}

export const AnswerForm: FC<P> = ({ taskId, answerId, sectionId }) => {

  const form = useRef<QFormD>(null);

  const source = usePromise(
    Promise.all([taskSections(taskId), answerId ? answer(answerId) : Promise.resolve({})])
  );

  const [state, setState] = useState<S>({
    parent: Number(sectionId || 0), child: 0, answers: {}, opened: ["0"]
  });

  // 将state中的answer与获取到的answer数据同步
  useEffect(() => {
    if (!source.loaded || source.error)
      return;
    setState({ ...state, answers: source.value![1] });
  }, [source]);

  // 选中其他问题页的时候更新index状态
  const select = (parent: number, child: number) => {
    const nextState: S = { ...state, parent, child };
    if (!opened.includes(parent.toString()))
      nextState.opened.push(parent.toString());
    setState(nextState);
  };

  // parent和child存储的是section从0开始的index而非问题的id，做从index到[一级section id]/[二级section id]的转化
  const selectedAnswerId = (parent: number, child: number) =>
    `${layout[parent].id}/${layout[parent].sections![child].id}`;

  // 本地暂存回答内容
  const saveLocal = () => {
    if (!form.current)
      return null;
    return form.current.submit().then(values => {
      let pIdx = Number(parent), cIdx = Number(child);
      answers[selectedAnswerId(parent, child)] = values;
      if (cIdx + 1 !== layout[pIdx].sections!.length)
        ++cIdx;
      else if (pIdx + 1 !== layout.length) {
        ++pIdx;
        cIdx = 0;
      }
      select(pIdx, cIdx);
      return values;
    });
  };

  // 内容推送至服务器
  const saveRemote = () => {
    const promise = saveLocal();
    if (!promise)
      return;
    promise.then(() => postAnswer(taskId, answers), err => message.error(err.message))
      .then(() => message.success("表单数据提交成功"));
  };

  if (!source.loaded)
    return <PageLoading/>;
  if (source.error)
    return null;

  const { parent, child, opened, answers } = state;
  const layout = source.value![0];

  return <Layout>
    <Layout.Sider style={{ width: 200, background: "#fff" }}>
      <Menu mode="inline" selectedKeys={[`${parent}/${child}`]} defaultOpenKeys={opened}
            style={{ height: "100%", borderRight: 0 }}
      >
        {
          layout.map(({ id, sections, title }, idx) =>
            <Menu.SubMenu key={idx} title={<span><Icon type="bars"/>{title}</span>}>
              {
                sections && sections.map((sec, ndx) =>
                  <Menu.Item key={`${idx}/${ndx}`} onClick={() => select(idx, ndx)}>{sec.title}</Menu.Item>
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
        <QForm source={layout[parent].sections![child]}
               answer={answers[selectedAnswerId(parent, child)]}
               wrappedComponentRef={form} onSave={saveLocal} onSubmit={saveRemote}
        />
      </Layout.Content>
    </Layout>
  </Layout>;

};

export default AnswerForm;
