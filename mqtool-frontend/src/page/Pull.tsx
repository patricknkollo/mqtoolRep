import React, { useState } from "react";
import ListOfQueues from "../component/ListOfQueues";
import axios from "axios";

interface FormProps {
  onSubmit: (data: UserData) => void;
}

interface UserData {
  queuename: string;
  filename: string;
  number: number;
  copy: boolean;
}

function Pull() {
  const [formData, setFormData] = React.useState<UserData>({
    queuename: "",
    filename: "",
    number: 0,
    copy: false,
  });

  const handleSelectChange = (event: React.ChangeEvent<HTMLSelectElement>) => {
    const { name, value } = event.target;
    setFormData({ ...formData, [name]: value });
  };
  const handleInputChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = event.target;
    setFormData({ ...formData, [name]: value });
  };

  const [isChecked, setIsChecked] = useState<boolean>(false);
  const handleCheck = (event: React.ChangeEvent<HTMLInputElement>) => {
    setIsChecked(event.target.checked);
    formData.copy = !isChecked;
  };

  function handleSubmit(event: React.FormEvent<HTMLFormElement>) {
    event.preventDefault();
    console.log(formData);
    try {
      const response = axios({
        method: "post",
        url: "http://localhost:8080/pullMessage",
        data: formData,
        headers: { "Content-Type": "text/form-data" },
      });
    } catch (error) {
      console.log(error);
    }
    setFormData({ queuename: "", filename: "", number: 0, copy: false });
  }

  return (
    <div className="row">
      <div className="column">
        <div className="card">
          <h3>Pull messages locally from a Queue</h3>
          <p>Select Queue name in dropdown</p>
          <form onSubmit={handleSubmit}>
            <div className="inputs">
              <label className="label">Queue name:</label>
              <ListOfQueues onChange={handleSelectChange} />
              <label className="label">File name:</label>
              <input
                className="input"
                placeholder="Enter file name to save"
                id="filename"
                name="filename"
                onChange={handleInputChange}
              />
            </div>
            <div className="inputs">
              <label className="label">Number of Messages:</label>
              <input
                className="input"
                type="number"
                placeholder="Enter number of messages"
                id="number"
                name="number"
                onChange={handleInputChange}
              />
            </div>
            <div className="inputs">
              <label className="label">Copy from Queue:</label>
              <input
                type="checkbox"
                className="checkbox"
                id="copy"
                name="copy"
                onChange={handleCheck}
                checked={isChecked}
              />
            </div>
            <div>
              <button className="button">Pull</button>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
}

export default Pull;
