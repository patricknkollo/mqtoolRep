import React, { useState, useEffect } from "react";
import { RabbitMqService } from "../service/RabbitMqService";

interface IState {
  loading: boolean;
  queues: string[];
  errorMsg: string;
}

interface ChildComponentProps {
  onChange: (event: React.ChangeEvent<HTMLSelectElement>) => void;
}

const ListOfQueues: React.FC<ChildComponentProps> = ({ onChange }) => {
  const [state, setState] = useState<IState>({
    loading: false,
    queues: [] as string[],
    errorMsg: "",
  });
  useEffect(() => {
    setState({ ...state, loading: true });
    RabbitMqService.getAllQueues()
      .then((res) =>
        setState({
          ...state,
          loading: false,
          queues: res.data,
        })
      )
      .catch((err) =>
        setState({
          ...state,
          loading: false,
          errorMsg: err.message,
        })
      );
  }, []);

  const { loading, queues, errorMsg } = state;

  return (
    <>
      <div className="container">
        {errorMsg && <p>{errorMsg}</p>}
        {loading && <h5>Loading...</h5>}
        <select
          className="select-queue"
          id="queuename"
          name="queuename"
          onChange={onChange}
        >
          {queues.length > 0 &&
            queues.map((queuename) => <option>{queuename}</option>)}
        </select>
      </div>
    </>
  );
};

export default ListOfQueues;
