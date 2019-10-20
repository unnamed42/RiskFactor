export interface QuestionOption {
  required?: boolean;
  enabler?: boolean;
  prefixPostfix?: boolean;
  defaultSelected?: number;
  additionalDescription?: string;
  filterKey?: string;
  placeholderText?: string;
  errorMessage?: string;
}

export interface Question {
  type: "TEXT" | "NUMBER" | "DATE" | "CHOICE" | "IMMUTABLE" |
      "YESNO_CHOICE" | "LIST" | "LIST_APPENDABLE" |
      "MULTI_CHOICE" | "SINGLE_CHOICE" |
      "MULTI_SELECT" | "SINGLE_SELECT";
  fieldName: string;
  description?: string;
  option?: QuestionOption;
  list?: Question[];
}

export interface Section {
  title: string;
  questions: Question[];
}
