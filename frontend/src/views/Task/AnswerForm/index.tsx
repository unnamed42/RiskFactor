import React, { FC, useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import { RouteComponentProps, withRouter } from "react-router-dom";

import { Icon, Layout, Menu, message, PageHeader } from "antd";
import { merge, isEmpty, debounce } from "lodash";

import { PageLoading } from "@/components";
import { QForm } from "./QForm";

import { answer, postAnswer, updateAnswer, taskSections, taskMtime } from "@/api/task";
import { firstKey, useEffectAsync } from "@/utils";
import { Question, Section } from "@/types";
import { StoreType } from "@/redux";
import * as taskStore from "@/redux/task";

const travel = (set: Set<string>, q: Question): void => {
  set.add(q.id.toString());
  q.list?.forEach(q1 => travel(set, q1));
};

// 收集Section下所有Question的id
const collectQId = (sec: Section) => {
  const result = new Set<string>();
  sec.questions?.forEach(q => travel(result, q));
  return result;
};

// 在控件中的answer state是按照 { "[一级标题]/[二级标题]": { "问题id": "问题回复" } } 的格式组织的，而
// 后端api中返回的回答是 { "问题id": "问题回复" } 格式，即所有Section下的回答合并到一起。
// 该函数将后端api的格式转化为控件需要的按Section分隔的格式
const putAnswers = (sections: Section[], values: KV<string>) => sections.reduce((acc: any, sec) => {
  const [h1, h2] = sec.title.split("/");
  const sectionAnswers: any = {};
  collectQId(sec).forEach(id => {
    if (id in values)
      sectionAnswers[id] = values[id];
  });
  return Object.assign(acc, { [`${h1}/${h2}`]: sectionAnswers });
}, {});

const formatSections = (sections: Section[]) => sections.reduce((obj: KV<KV<Section>>, sec) => {
  const [h1, h2] = sec.title.split("/");
  return merge(obj, { [h1]: { [h2]: sec } });
}, {});

interface P extends RouteComponentProps {
  taskId: number | string;
  answerId?: number | string;
  sectionId?: number | string;
}

interface KV<T> {
  [key: string]: T;
}

interface Headers {
  h1: string;
  h2: string;
}

export const AnswerForm = withRouter<P, FC<P>>(({ taskId, history, ...props }) => {

  const cache = useSelector((state: StoreType) => state.task);
  const dispatch = useDispatch();

  // 问卷样式，之后再获取
  const [layout, setLayout] = useState<KV<KV<Section>>>({});
  // 创建新answer时此项为undefined，其余情况应该均有值
  const [answerId, setAnswerId] = useState(props.answerId);
  // 修改内容，更新回答时使用。如后端api一般，扁平而非按Section分层的结构
  const [patches, setPatches] = useState<KV<string>>({});
  // 当前选中的 [一级标题(h1)]/[二级标题(h2)]
  const [{ h1, h2 }, setHeaders] = useState<Headers>({ h1: "", h2: "" });
  // 该项目的所有回答，按照 { "[一级标题]/[二级标题]": { "问题id": "问题回复" } } 形式组织
  const [answers, setAnswers] = useState<KV<KV<string>>>({});
  // 加载完成时的指示器
  const [loaded, setLoaded] = useState(false);

  // componentDidMount()
  useEffectAsync(async () => {
    try {
      // task的更新时间
      const { mtime } = await taskMtime(taskId);
      const cached = cache[taskId];
      // 如果已经缓存最新样式，则设置样式，否则重新获取
      const sections = cached?.mtime === mtime ? cached.layout : await taskSections(taskId);
      const formatted = formatSections(sections);
      // 将样式存入store中
      dispatch(taskStore.update(taskId, mtime, sections));
      // 将样式更新到state
      setLayout(formatted);
      const header1 = firstKey(formatted);
      const header2 = header1 != null ? firstKey(formatted[header1]) : null;
      setHeaders({ h1: header1 ?? "", h2: header2 ?? "" });

      // 当存在answerId时，从后端获取答案值
      if (answerId !== undefined) {
        const values = await answer(answerId);
        setAnswers(putAnswers(sections, values));
      }

      setLoaded(true);
    } catch (err) {
      message.error(err.message);
    }
  }, []);

  const page = () => `${h1}/${h2}`;

  const valuesChanged = (changes: any) => {
    const curr = page();
    const currAnswer = answers[page()];
    const newAnswers = Object.assign(answers, {
      [curr]: { ...currAnswer, ...changes }
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
  }, 100, { leading: true });

  const pageChanged = (newH1: string, newH2: string) => {
    setHeaders({ h1: newH1, h2: newH2 });
  };

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
        <QForm layout={layout[h1][h2]} answer={answers[page()]}
               onChange={valuesChanged} onSubmit={post} />
      </Layout.Content>
    </Layout>
  </Layout>;

});

export default AnswerForm;
