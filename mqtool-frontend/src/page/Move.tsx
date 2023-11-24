import React, { useState } from "react";
import ListOfQueues from "../component/ListOfQueues";
import axios from "axios";

interface FormProps {
  onSubmit: (data: UserData) => void;
}

interface UserData {
  source_queuename: string;
  target_queuename: string;
  copy: boolean;
  number: number;
  regex: string;
}

function Move() {
  const [formData, setFormData] = React.useState<UserData>({
    source_queuename: "",
    target_queuename: "",
    copy: false,
    number: 0,
    regex: "",
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
    setFormData({
      source_queuename: "",
      target_queuename: "",
      copy: false,
      number: 0,
      regex: "",
    });
  }

  return (
    <div className="row">
      <div className="column">
        <div className="card">
          <h3>Move messages from source queue to target queue</h3>
          <p>Select Source and Target queuename in dropdown</p>
          <form onSubmit={handleSubmit}>
            <div className="inputs">
              <label className="queue">Source Queue:</label>
              <ListOfQueues onChange={handleSelectChange} />
            </div>
            <div className="inputs">
              <label className="queue">Target Queue:</label>
              <ListOfQueues onChange={handleSelectChange} />
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
              <label className="label">Regex Check</label>
              <input
                className="input"
                type="regex"
                placeholder="Enter Regex"
                id="regex"
                name="regex"
                onChange={handleInputChange}
              />
            </div>
            <div className="inputs">
              <label className="label">Move copy of messages:</label>
              <input
                type="checkbox"
                className="checkbox"
                id="copy"
                name="copy"
                onChange={handleCheck}
                checked={isChecked}
              />
            </div>

            <button className="button">Move</button>
          </form>
        </div>
      </div>
    </div>
  );
}
export default Move;
