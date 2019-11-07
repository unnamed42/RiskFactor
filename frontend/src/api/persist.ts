const setOrRemove = (key: string, value: string | null) => {
  if (value)
    localStorage.setItem(key, value);
  else
    localStorage.removeItem(key);
};

const auth = {
  get token() { return localStorage.getItem("auth.token"); },
  set token(t: string | null) { setOrRemove("auth.token", t); }
};

export const local = {
  auth
};
