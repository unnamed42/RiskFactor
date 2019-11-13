import React, { Component } from "react";

import { Icon, Layout, Menu, message, PageHeader } from "antd";
import { merge, isEmpty } from "lodash";

import { PageLoading } from "@/components";
import { QForm } from "./QForm";

import { answer, postAnswer, updateAnswer, taskSections } from "@/api/task";
import { firstKey } from "@/utils";
import { Question, Section } from "@/types";

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
    if(id in values)
      sectionAnswers[id] = values[id];
  });
  return Object.assign(acc, { [`${h1}/${h2}`]: sectionAnswers });
}, {});

interface P {
  taskId: number | string;
  answerId?: number | string;
  sectionId?: number | string;
}

interface KV<T> {
  [key: string]: T;
}

interface S {
  // 用于指示是否应该重新渲染。当需要重新渲染时，更新这个state
  id: number;
  h1: string;
  h2: string;
  // 创建新answer时此项为undefined，其余情况应该均有值
  answerId?: number | string;
  answers: KV<KV<string>>;
  // 修改内容，更新回答时使用
  // 如后端api一般，扁平而非按Section分层的结构
  patches: KV<string>;
  layout?: KV<KV<Section>>;
}

// TODO: 问卷的样式不用每次都获取，后端需要给Section或Task加一个mtime
//       然后比较mtime，当有更新时再去重新拉一个样式下来
export class AnswerForm extends Component<P, S> {

  constructor(props: P) {
    super(props);
    this.state = { id: 0, h1: "", h2: "", answers: {}, patches: {}, answerId: props.answerId };
  }

  componentDidMount() {
    const { taskId, answerId } = this.props;
    // 当存在answerId时，从后端获取预设答案值
    const answerPromise = answerId !== undefined ? answer(answerId) : Promise.resolve({});
    // 自后端api获取问卷内容和（可选）答案内容
    Promise.all<Section[], any>([taskSections(taskId), answerPromise]).then(([sections, answers]) => {
      const layout = sections.reduce((obj: KV<KV<Section>>, sec) => {
        const [h1, h2] = sec.title.split("/");
        return merge(obj, { [h1]: { [h2]: sec } });
      }, {});
      const h1 = firstKey(layout);
      const h2 = (h1 !== null ? firstKey(layout[h1]) : null) ?? "";
      this.setState({ id: this.state.id + 1, h1: h1 ?? "", h2, layout, answers: putAnswers(sections, answers) });
    }).catch((err: Error) => message.error(err.message));
  }

  shouldComponentUpdate(nextProps: Readonly<P>, nextState: Readonly<S>): boolean {
    return this.state.id !== nextState.id;
  }

  render() {
    const { layout, h1, h2, answers } = this.state;
    if(!layout)
      return <PageLoading />;

    const { taskId } = this.props;

    return <Layout>
      <Layout.Sider style={{ width: 200, background: "#fff" }}>
        <Menu mode="inline" selectedKeys={[this.path()]} defaultOpenKeys={[h1]}
              style={{ height: "100%", borderRight: 0 }}>
          {
            Object.keys(layout).map(t1 =>
              <Menu.SubMenu key={t1} title={<span><Icon type="bars"/>{t1}</span>}>
                {
                  Object.keys(layout[t1]).map(t2 =>
                    <Menu.Item key={`${t1}/${t2}`} onClick={() => this.pageChanged(t1, t2)}>
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
          <QForm layout={layout[h1][h2]} answer={answers[`${h1}/${h2}`]}
                 onChange={changes => this.valuesChanged(changes)} onSubmit={values => this.post(values)} />
        </Layout.Content>
      </Layout>
    </Layout>;
  }

  private async post(values: any) {
    const { taskId } = this.props;
    const { answerId } = this.state;
    try {
      if(answerId === undefined) {
        const { id } = await postAnswer(taskId, values);
        message.success("提交成功");
        this.setState({ answerId: id });
      } else {
        if(!isEmpty(this.state.patches)) {
          await updateAnswer(answerId, this.state.patches);
          message.success("更新成功");
          this.setState({ patches: {} });
        } else
          message.info("没有更新内容，无需提交");
      }
    } catch (e) {
      message.error(e.message);
    }
  }

  private path() {
    const { h1, h2 } = this.state;
    return `${h1}/${h2}`;
  }

  private valuesChanged(changes: any) {
    const page = this.path();
    const curr = this.state.answers[page];
    const answers = Object.assign(this.state.answers, {
      [page]: { ...curr, ...changes }
    });
    const patches = Object.assign(this.state.patches, changes);
    this.setState({ answers, patches });
  }

  private pageChanged(h1: string, h2: string) {
    this.setState({
      id: this.state.id + 1,
      h1, h2
    });
  }

}

export default AnswerForm;
