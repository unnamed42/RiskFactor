import React, { Component } from "react";

import { Question, QProps } from ".";
import { Question as QSchema } from "@/types/task";

interface P extends Omit<QProps, "schema"> {
  list: QSchema[];
}

export class QList extends Component<P> {
  render() {
    const { list } = this.props;
    return <div>
      {list.map(q => <Question key={q.id} schema={q}/>)}
    </div>;
  }
}
