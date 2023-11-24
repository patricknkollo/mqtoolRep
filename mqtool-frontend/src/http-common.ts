import axios from "axios";

export default axios.create({
  method: "post",
  baseURL: "http://localhost:8080/",
  headers: { "Content-Type": "multipart/form-data" },
});