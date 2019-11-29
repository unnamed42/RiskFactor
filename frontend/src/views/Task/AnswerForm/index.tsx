import React, { FC, useState } from "react";
import { useHistory } from "react-router-dom";

import { Icon, Layout, Menu, message, PageHeader } from "antd";
import { assign, isEmpty } from "lodash";

import { Loading } from "@/components";
import { QForm } from "./QForm";

import { answer, postAnswer, updateAnswer } from "@/api/task";
import { firstKey, usePromise, cachedLayout, cachedStructure } from "@/utils";
import { Dict } from "@/types";

interface P {
  taskId: number | string;
  answerId?: number | string;
  sectionId?: number | string;
}

export const AnswerForm: FC<P> = ({ taskId, sectionId, ...props }) => {
  const history = useHistory();

  // 创建新answer时此项为undefined，其余情况应该均有值
  const [answerId, setAnswerId] = useState(props.answerId);
  // 修改内容，更新回答时使用。如后端api一般，扁平而非按Section分层的结构
  const [patches, setPatches] = useState<Dict<string>>({});

  const [state, updateState] = usePromise(async () => {
    // [问卷样式, 问卷大纲结构]
    const [layout, struct] = await Promise.all([
      cachedLayout(taskId), cachedStructure(taskId)
    ]);
    // 当前选中的 [一级标题(h1)]/[二级标题(h2)]/...
    const header = firstKey(layout) ?? "";
    // 该项目的所有回答，按照 { "[一级标题]/[二级标题]": { "问题id": "问题回复" } } 形式组织
    let answers: Dict = { "#vars": { answerId } };
    if(answerId !== undefined)
      assign(answers, await answer(answerId));
    return { layout, struct, header, answers };
  }, e => message.error(e.message));

  if (state.loaded === null)
    return null;
  if (!state.loaded)
    return <Loading/>;

  const { layout, struct, answers, header } = state;

  const valuesChanged = (changes: any, allValues: any) => {
    const newAnswers = { ...answers, ...allValues };
    const newPatches = { ...patches, ...changes };
    updateState({ answers: newAnswers });
    console.log(newAnswers);
    setPatches(newPatches);
  };

  const post = async () => {
    if(isEmpty(patches)) {
      message.info("没有更新内容，无需提交");
      return;
    }
    try {
      if (answerId === undefined) {
        const { id } = await postAnswer(taskId, answers);
        setAnswerId(id); setPatches({});
        updateState({ answers: { ...answers, "#vars": { answerId: id } } });
        history.replace(`${history.location.pathname}/${id}`);
        message.success("提交成功");
      } else {
        await updateAnswer(taskId, answerId, answers);
        setPatches({});
        message.success("更新成功");
      }
    } catch (e) { message.error(e.message); }
  };

  const renderedMenu = struct.map(({ name, list }) => {
    if(!list || list.length === 0)
      return <Menu.Item key={name} onClick={() => updateState({ header: name })}>{name}</Menu.Item>;
    return <Menu.SubMenu key={name} title={<span><Icon type="bars"/>{name}</span>}>
      {
        list.map(s2 =>
          <Menu.Item key={`${name}/${s2.name}`} onClick={() => updateState({ header: `${name}/${s2.name}` })}>
            {s2.name}
          </Menu.Item>)
      }
    </Menu.SubMenu>;
  });

  const parts = header.split("/");

  return <Layout>
    <Layout.Sider style={{ width: 200, background: "#fff" }}>
      <Menu mode="inline" selectedKeys={[header]} defaultOpenKeys={parts.length === 1 ? undefined : [parts[0]]}
            style={{ height: "100%", borderRight: 0 }}>
        {renderedMenu}
      </Menu>
    </Layout.Sider>
    <Layout>
      <Layout.Content style={{ padding: "20px 24px", minHeight: 280 }}>
        <PageHeader title="返回数据页" onBack={() => history.replace(`/task/${taskId}/answers`)}/>
        <QForm layout={layout[header]} answer={answers}
               onChange={valuesChanged} onSubmit={post} />
      </Layout.Content>
    </Layout>
  </Layout>;
};

export default AnswerForm;
