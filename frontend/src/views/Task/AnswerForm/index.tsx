import React, { FC, useState } from "react";
import { RouteComponentProps, withRouter } from "react-router-dom";

import { Icon, Layout, Menu, message, PageHeader } from "antd";
import { assign, isEmpty, debounce, flatMap } from "lodash";

import { PageLoading } from "@/components";
import { QForm } from "./QForm";

import {
  answer, postAnswer, updateAnswer,
  taskLayout, taskStructure
} from "@/api/task";
import { appendArray, firstKey, usePromise } from "@/utils";
import { Dict, Question } from "@/types";
import { cacheSelector } from "@/views/util";

// 收集所有Question的id。如果有list，递归进行
const collectQId = (qs: Question[]) => {
  const result = new Set<string>();
  for(let arr = qs; arr.length !== 0; ) {
    let next: Question[] = [];
    arr.forEach(({ id, list }) => {
      result.add(id.toString());
      if(list) next = appendArray(next, list);
    });
    arr = next;
  }
  return result;
};

// 在控件中的answer state是按照 { "[一级标题]/[二级标题]": { "问题id": "问题回复" } } 的格式组织的，而
// 后端api中返回的回答是 { "问题id": "问题回复" } 格式，即所有Section下的回答合并到一起。
// 该函数将后端api的格式转化为控件需要的按Section分隔的格式
const putAnswers = (layout: Dict<Question[]>, values: Dict<string>): Dict<Dict<string>> =>
  Object.entries(layout).reduce((obj: Dict<Dict<string>>, [title, list]) => {
    const sectionAnswers: Dict<string> = {};
    if(list === undefined)
      return obj;
    collectQId(list).forEach(id => {
      if(id in values)
        sectionAnswers[id] = values[id];
    });
    return assign(obj, { [title]: sectionAnswers });
  }, {});

// 将所有内容归约为 { [一级标题/二级标题/三级标题...]: 问题list, ... }
const formatLayout = (questions: Question[]): Dict<Question[]> => {
  // 默认第一层的Question type应该为header
  // 将第一层map成 { header的label: header的list } 形式，成一个数组
  let init = questions.map(q => {
    if(q.type !== "header" || !q.list)
      throw new Error(`questions in first layer is not a valid header`);
    return { title: q.label ?? "", list: q.list };
  });
  // 如果list中还有header，也将其展开成上述格式并合并到数组中来
  for(;;) {
    let changed = false;
    init = flatMap(init, elem => {
      const { title, list } = elem;
      if(list[0].type !== "header")
        return elem;
      changed = true;
      // 只要list中有一个是header，那么认为都是header
      return list.map(q => {
        if(q.type !== "header" || !q.list)
          throw new Error(`question is not a header`);
        return { title: `${title}/${q.label ?? ""}`, list: q.list };
      });
    });
    if(!changed) break;
  }
  // 将 { title, list } 数组合并成Map
  return init.reduce((obj: Dict<Question[]>, { title, list }) => assign(obj, { [title]: list }), {});
};

interface P extends RouteComponentProps {
  taskId: number | string;
  answerId?: number | string;
  sectionId?: number | string;
}

export const AnswerForm = withRouter<P, FC<P>>(({ taskId, history, ...props }) => {

  // 创建新answer时此项为undefined，其余情况应该均有值
  const [answerId, setAnswerId] = useState(props.answerId);
  // 修改内容，更新回答时使用。如后端api一般，扁平而非按Section分层的结构
  const [patches, setPatches] = useState<Dict<string>>({});

  const [state, updateState] = usePromise(async () => {
    // 问卷样式，之后再获取
    const layout = await cacheSelector(taskId, "layout",
      async () => formatLayout(await taskLayout(taskId)));
    // 问卷大纲结构
    const struct = await cacheSelector(taskId, "struct",
      async () => taskStructure(taskId));
    // 当前选中的 [一级标题(h1)]/[二级标题(h2)]/...
    const header: string = firstKey(layout) ?? "";
    // 该项目的所有回答，按照 { "[一级标题]/[二级标题]": { "问题id": "问题回复" } } 形式组织
    let answers: Dict<Dict<string>> = {};
    if(answerId !== undefined) {
      const values = await answer(answerId);
      answers = putAnswers(layout, values);
    }
    console.log(header);
    return { layout, struct, header, answers };
  }, e => message.error(e.message));

  if (state.loaded === null)
    return null;
  if (!state.loaded)
    return <PageLoading/>;

  const { layout, struct, answers, header } = state;

  const valuesChanged = (changes: any) => {
    const currAnswer = answers[header];
    const newAnswers = Object.assign(answers, {
      [header]: { ...currAnswer, ...changes }
    });
    const newPatches = Object.assign(patches, changes);
    updateState({ answers: newAnswers });
    setPatches(newPatches);
  };

  // 或者用throttle？
  const post = debounce(async (values: any) => {
    if(isEmpty(patches)) {
      message.info("没有更新内容，无需提交");
      return;
    }
    try {
      if (answerId === undefined) {
        const { id } = await postAnswer(taskId, values);
        setAnswerId(id); setPatches({});
        message.success("提交成功");
        history.replace(`${history.location.pathname}/${id}`);
      } else {
        await updateAnswer(answerId, patches);
        setPatches({});
        message.success("更新成功");
      }
    } catch (e) { message.error(e.message); }
  }, 200, { leading: true });

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
        <PageHeader title="返回数据页" onBack={() => window.location.hash = `/task/${taskId}/answers`}/>
        <QForm layout={layout[header]} answer={answers[header]}
               onChange={valuesChanged} onSubmit={post} />
      </Layout.Content>
    </Layout>
  </Layout>;

});

export default AnswerForm;
