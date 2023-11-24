import { BrowserRouter as Router, Route, Routes } from "react-router-dom";
import Navbar from "./layout/Navbar";
import Help from "./page/Help";
import Push from "./page/Push";
import Pull from "./page/Pull";
import Move from "./page/Move";
import Footer from "./layout/Footer";
import Intro from "./page/Intro";

function App() {
  return (
    <Router>
      <div className="">
        <Navbar />
        <div className="">
          <Routes>
            <Route path="" element={<Intro />} />
            <Route path="/push" element={<Push />} />
            <Route path="/pull" element={<Pull />} />
            <Route path="/move" element={<Move />} />
            <Route path="/help" element={<Help />} />
          </Routes>
        </div>
        <Footer />
      </div>
    </Router>
  );
}

export default App;
