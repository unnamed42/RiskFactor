import React, { FC, useState, useEffect, useRef } from "react";

import { message, Layout, Menu, Icon, PageHeader } from "antd";

import { PageLoading } from "@/components";
import { QForm, QFormD } from "./QForm";

import { answer, postAnswer, taskSections } from "@/api/task";
import { firstKey, usePromise } from "@/utils";
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
  layout: QLayout;
}

export const AnswerForm: FC<P> = ({ taskId, answerId, sectionId }) => {

  const form = useRef<QFormD>(null);

  const source = usePromise(() =>
    // 推断不出类型？？你又bug了？
    Promise.all<Section[], any>([
      taskSections(taskId),
      (answerId !== undefined ? answer(answerId) : Promise.resolve({}))
    ])
  );

  const [state, setState] = useState<S>();

  // 将state中的answer与获取到的answer数据同步
  useEffect(() => {
    if (!source.loaded || "error" in source)
      return;
    const [sections, ans] = source.value;
    const layout = sections.reduce((obj, sec) => {
      const [h1, h2] = sec.title.split("/");
      if(!(h1 in obj))
        obj[h1] = {};
      obj[h1][h2] = sec;
      return obj;
    }, {} as QLayout);
    const h1 = firstKey(layout);
    const h2 = h1 !== null ? firstKey(layout[h1]) : null;
    setState({ h1: h1 ?? "", h2: h2 ?? "", layout, answers: ans });
  }, [source]);

  if (!source.loaded || !state)
    return <PageLoading/>;
  if ("error" in source)
    return null;

  const { h1, h2, answers, layout } = state;

  const saveCurrent = () =>
    form.current?.submit().then(ans =>
      setState({ h1, h2, layout, answers: { ...answers, [`${h1}/${h2}`]: ans } })
    );

  return <Layout>
    <Layout.Sider style={{ width: 200, background: "#fff" }}>
      <Menu mode="inline" selectedKeys={[`${h1}/${h2}`]} defaultOpenKeys={[h1]}
            style={{ height: "100%", borderRight: 0 }}
      >
        {
          Object.keys(layout).map(h1 =>
            <Menu.SubMenu key={h1} title={<span><Icon type="bars"/>{h1}</span>}>
              {
                Object.keys(layout[h1]).map(h2 =>
                  <Menu.Item key={`${h1}/${h2}`} onClick={() => setState({ ...state, h1, h2 })}>
                    {h2}
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
        <PageHeader title=" " onBack={() => window.location.hash = `/task/${taskId}/answers`}/>
        <QForm source={layout[h1][h2]}
               answer={answers[`${h1}/${h2}`]}
               wrappedComponentRef={form} onSave={saveCurrent}
        />
      </Layout.Content>
    </Layout>
  </Layout>;

};

export default AnswerForm;
