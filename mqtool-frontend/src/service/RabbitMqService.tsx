import axios from 'axios'
export class RabbitMqService{
    private static URL:string = 'http://localhost:8080'

    public static getAllQueues(){
        let UserURL:string = `${this.URL}/list` 
        return axios.get(UserURL)
    }
}