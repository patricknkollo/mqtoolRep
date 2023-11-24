import React, { useState } from "react";
import FileUpload from "../component/FileUpload";
import ListOfQueues from "../component/ListOfQueues";
import axios from "axios";

interface FormProps {
  onSubmit: (data: UserData) => void;
}

interface UserData {
  queuename: string;
  textfile: File;
}

function Push() {
  const [formData, setFormData] = useState<UserData>({
    queuename: "",
    textfile: new File([], ""),
  });

  const handleSelectChange = (event: React.ChangeEvent<HTMLSelectElement>) => {
    const { name, value } = event.target;
    setFormData({ ...formData, [name]: value });
  };

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    if (e.target.files) {
      const file = e.target.files[0];
      formData.textfile = file;
    }
  };

  function handleSubmit(event: React.FormEvent<HTMLFormElement>) {
    event.preventDefault();
    console.log(formData);
    try {
      const response = axios({
        method: "post",
        url: "http://localhost:8080/pushTextFile",
        data: formData,
        headers: { "Content-Type": "multipart/form-data" },
      });
    } catch (error) {
      console.log(error);
    }
    setFormData({ queuename: "", textfile: new File([], "") });
    Array.from(document.querySelectorAll("input")).forEach(
      (input: HTMLInputElement) => (input.value = "")
    );
    /* still needs figure out dropdown reset after submit*/
  }

  return (
    <div className="row">
      <div className="column">
        <div className="card">
          <h3>Push messages from text file to the RabbitMQ queue.</h3>
          <p>
            Select queue name in dropdown and browse the text file to upload.
          </p>
          <form onSubmit={handleSubmit}>
            <div className="inputs">
              <label className="label">Queue Name:</label>
              <ListOfQueues onChange={handleSelectChange} />
            </div>
            <div className="inputs">
              <label className="label">Filepath:</label>
              <FileUpload onChange={handleChange} />
            </div>
            <button
              className="button"
              type="submit"
              disabled={!(formData.queuename && formData.textfile)}
            >
              Push
            </button>
          </form>
        </div>
      </div>
    </div>
  );
}

export default Push;
