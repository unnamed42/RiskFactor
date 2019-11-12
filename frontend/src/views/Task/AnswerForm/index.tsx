import React, { Component, createRef, FC } from "react";

import { message, Layout, Menu, Icon, PageHeader } from "antd";
import { merge, assign } from "lodash";

import { PageLoading } from "@/components";
import { QForm, QFormD } from "./QForm";

import { answer, taskSections } from "@/api/task";
import { firstKey } from "@/utils";
import { Section } from "@/types";

interface P {
  taskId: number | string;
  answerId?: number | string;
  sectionId?: number | string;
}

interface QLayout {
  [key: string]: {
    [key: string]: Section;
  };
}

interface S {
  h1: string;
  h2: string;
  answers: any;
  layout?: QLayout;
  error?: string;
}

export class AnswerForm extends Component<P, S> {
  form = createRef<QFormD>();

  constructor(props: P) {
    super(props);
    this.state = { h1: "", h2: "", answers: {} };
  }

  componentDidMount() {
    const { taskId, answerId } = this.props;
    const answerPromise = answerId !== undefined ? answer(answerId) : Promise.resolve({});
    // 自后端api获取问卷内容和（可选）答案内容
    Promise.all<Section[], any>([taskSections(taskId), answerPromise]).then(([sections, answers]) => {
      const layout = sections.reduce((obj, sec) => {
        const [h1, h2] = sec.title.split("/");
        return merge(obj, { [h1]: { [h2]: sec } });
      }, {} as QLayout);
      const h1 = firstKey(layout);
      const h2 = h1 !== null ? firstKey(layout[h1]) : null;
      this.setState({ h1: h1 ?? "", h2: h2 ?? "", layout, answers });
    }).catch((err: Error) => this.setState({ error: err.message }));
  }

  saveCurrent() {
    this.form.current?.validatedValues().then(ans => {
      const { answers, h1, h2 } = this.state;
      const newAns = assign(answers, { [`${h1}/${h2}`]: ans });
      this.setState({ answers: newAns });
    });
  }

  render() {
    const { layout, error, h1, h2, answers } = this.state;
    if(error !== undefined) {
      message.error(error);
      return null;
    }
    if(layout === undefined)
      return <PageLoading />;

    const { taskId } = this.props;

    return <Layout>
      <Layout.Sider style={{ width: 200, background: "#fff" }}>
        <Menu mode="inline" selectedKeys={[`${h1}/${h2}`]} defaultOpenKeys={[h1]}
              style={{ height: "100%", borderRight: 0 }}>
          {
            Object.keys(layout).map(t1 =>
              <Menu.SubMenu key={t1} title={<span><Icon type="bars"/>{t1}</span>}>
                {
                  Object.keys(layout[t1]).map(t2 =>
                    <Menu.Item key={`${t1}/${t2}`} onClick={() => this.setState({ h1: t1, h2: t2 })}>
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
          <QForm layout={layout[h1][h2]}
                 answer={answers[`${h1}/${h2}`]}
                 wrappedComponentRef={this.form} onSave={() => this.saveCurrent()}
          />
        </Layout.Content>
      </Layout>
    </Layout>;
  }

  private renderNavigationMenu = (layout: QLayout) => {

  };
}

export default AnswerForm;
