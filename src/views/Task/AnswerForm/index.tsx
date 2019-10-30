import React, { FC, useState, useEffect, useRef } from "react";

import {message, Layout, Menu, Icon} from "antd";

import {PageLoading} from "@/components";
import { QForm, QFormD } from "./QForm";

import { Section } from "@/types/task";
import { taskSections } from "@/api/task";

interface P {
  taskId: number | string;
}

interface S {
  sections?: Section[];
  sectionPath?: string;
  answerId?: string | number;
}

export const AnswerForm: FC<P> = ({ taskId }) => {

  const [state, setState] = useState<S>();
  const form = useRef<QFormD>(null);

  useEffect(() => {
    taskSections(taskId).then(secs => {
      setState({ sectionPath: "0/0", sections: secs });
    }).catch((err: Error) => message.error(err.message));
  }, []);

  const selected = (parent: number, child: number) => {
    const sectionPath = `${parent}/${child}`;
    setState({ ...state, sectionPath });
  };

  const submit = () => {
    if(!form.current)
      return;
    form.current.submit().then((values) => {
      let pIdx = Number(parent), cIdx = Number(child);
      if(cIdx + 1 !== sections[pIdx].sections!.length)
        ++cIdx;
      else if(pIdx + 1 !== sections.length) {
        ++pIdx; cIdx = 0;
      }
      const path = `${pIdx}/${cIdx}`;
      setState({ ...state, sectionPath: path });
      console.log(values);
    }).catch(err => console.log(err));
  };

  if(!state || !state.sections || !state.sectionPath)
    return <PageLoading />;

  const { sectionPath, sections } = state;
  const [parent, child] = sectionPath.split("/");

  return <Layout>
    <Layout.Sider style={{ width: 200, background: "#fff" }}>
      <Menu mode="inline" selectedKeys={[sectionPath]} openKeys={[parent]}
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
        <QForm source={sections[Number(parent)].sections![Number(child)]}
          wrappedComponentRef={form} onSubmit={submit}
        />
      </Layout.Content>
    </Layout>
  </Layout>;

};

export default AnswerForm;
