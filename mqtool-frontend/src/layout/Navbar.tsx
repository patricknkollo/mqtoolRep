import React, { useState } from "react";
import { Link } from "react-router-dom";
import "../app.css";

function Navbar() {
  const [showDropdown, setShowDropdown] = useState(false);
  let items: string[] = ["Help"];

    return (
        <nav className="navbar">
            <div className="dropdown">
            <span className="drophead">Message</span>
            <div className="dropdown-content">
                <Link to="/pull" className="nav-link">Pull</Link>
                <Link to="/push" className="nav-link">Push</Link>
                <Link to="/move" className="nav-link">Move</Link>
            </div>
            </div>
            <div className="dropdown">
                <span className="help"><Link to="/help" className="nav-link">Help</Link></span>
            </div>
            
        </nav>
    );
}

export default Navbar;