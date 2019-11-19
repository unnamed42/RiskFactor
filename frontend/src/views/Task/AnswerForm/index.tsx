import React, { FC, useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import { RouteComponentProps, withRouter } from "react-router-dom";

import { Icon, Layout, Menu, message, PageHeader } from "antd";
import { assign, isEmpty, debounce, flatMap } from "lodash";

import { PageLoading } from "@/components";
import { QForm } from "./QForm";

import { answer, postAnswer, updateAnswer, taskQuestions, taskMtime } from "@/api/task";
import { firstKey, useEffectAsync, appendArray } from "@/utils";
import { KVPair, Question } from "@/types";
import { StoreType } from "@/redux";
import * as taskStore from "@/redux/task";

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
const putAnswers = (layout: Map<string, Question[]>, values: KVPair<string>): KVPair<KVPair<string>> => {
  let ret = {};
  for(const [title, list] of layout) {
    const sectionAnswers: KVPair<string> = {};
    collectQId(list).forEach(id => {
      if(id in values)
        sectionAnswers[id] = values[id];
    });
    ret = assign(ret, { [title]: sectionAnswers });
  }
  return ret;
};

// 将所有内容归约为 { [一级标题/二级标题/三级标题...]: 问题list, ... }
const formatLayout = (questions: Question[]): Map<string, Question[]> => {
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
  return new Map(init.map(({ title, list }) => [title, list]));
};

interface P extends RouteComponentProps {
  taskId: number | string;
  answerId?: number | string;
  sectionId?: number | string;
}

export const AnswerForm = withRouter<P, FC<P>>(({ taskId, history, ...props }) => {

  const cache = useSelector((state: StoreType) => state.task);
  const dispatch = useDispatch();

  // 问卷样式，之后再获取
  const [layout, setLayout] = useState<Map<string, Question[]>>();
  // 问卷目录结构
  const [struct, setStruct] = useState({});
  // 创建新answer时此项为undefined，其余情况应该均有值
  const [answerId, setAnswerId] = useState(props.answerId);
  // 修改内容，更新回答时使用。如后端api一般，扁平而非按Section分层的结构
  const [patches, setPatches] = useState<KVPair<string>>({});
  // 当前选中的 [一级标题(h1)]/[二级标题(h2)]/...
  const [header, setHeader] = useState("");
  // 该项目的所有回答，按照 { "[一级标题]/[二级标题]": { "问题id": "问题回复" } } 形式组织
  const [answers, setAnswers] = useState<KVPair<KVPair<string>>>({});
  // 加载完成时的指示器
  const [loaded, setLoaded] = useState(false);

  // componentDidMount()
  useEffectAsync(async () => {
    try {
      // task的更新时间
      const { mtime } = await taskMtime(taskId);
      const cached = cache[taskId];
      // 如果已经缓存最新样式，则设置样式，否则重新获取
      const layout = await (async () => {
        if(cached?.mtime === mtime)
          return cached.layout;
        else {
          const fetchedLayout = formatLayout(await taskQuestions(taskId));
          // 将新样式存入store中
          dispatch(taskStore.update(taskId, mtime, fetchedLayout));
          return fetchedLayout;
        }
      })();
      // 将样式更新到state
      setLayout(layout);
      setHeader(firstKey(layout) ?? "");

      // 当存在answerId时，从后端获取答案值
      if (answerId !== undefined) {
        const values = await answer(answerId);
        setAnswers(putAnswers(layout, values));
      }

      setLoaded(true);
    } catch (err) { message.error(err.message); }
  }, []);

  const valuesChanged = (changes: any) => {
    const currAnswer = answers[header];
    const newAnswers = Object.assign(answers, {
      [header]: { ...currAnswer, ...changes }
    });
    const newPatches = Object.assign(patches, changes);
    setAnswers(newAnswers);
    setPatches(newPatches);
  };

  const post = debounce(async (values: any) => {
    try {
      if (answerId === undefined) {
        const { id } = await postAnswer(taskId, values);
        setAnswerId(id); setPatches({});
        message.success("提交成功");
        history.replace(`${history.location.pathname}/${id}`);
      } else if (!isEmpty(patches)) {
        await updateAnswer(answerId, patches);
        setPatches({});
        message.success("更新成功");
      } else
        message.info("没有更新内容，无需提交");
    } catch (e) {
      message.error(e.message);
    }
  }, 200, { leading: true });

  if (!loaded)
    return <PageLoading/>;

  return <Layout>
    <Layout.Sider style={{ width: 200, background: "#fff" }}>
      <Menu mode="inline" selectedKeys={[page()]} defaultOpenKeys={[h1]}
            style={{ height: "100%", borderRight: 0 }}>
        {
          Object.keys(layout).map(t1 =>
            <Menu.SubMenu key={t1} title={<span><Icon type="bars"/>{t1}</span>}>
              {
                Object.keys(layout[t1]).map(t2 =>
                  <Menu.Item key={`${t1}/${t2}`} onClick={() => pageChanged(t1, t2)}>
                    {t2}
                  </Menu.Item>
                )
              }
            </Menu.SubMenu>
          )
        }
      </Menu>
    </Layout.Sider>
    <Layout>
      <Layout.Content style={{ padding: "20px 24px", minHeight: 280 }}>
        <PageHeader title="返回数据页" onBack={() => window.location.hash = `/task/${taskId}/answers`}/>
        <QForm layout={layout.get(header)} answer={answers[page()]}
               onChange={valuesChanged} onSubmit={post} />
      </Layout.Content>
    </Layout>
  </Layout>;

});

export default AnswerForm;
