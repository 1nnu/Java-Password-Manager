  import { Routes, Route } from "react-router-dom";
  import { LoginPage } from "./pages/Login";
  import { HomePage } from "./pages/Home";
  import { Register } from "./pages/Register"
  import React from 'react';

  import { RequireAuth } from "./components/RequireAuth";
  import { AuthProvider } from "./hooks/useAuth";
  import { Vault } from "./pages/Vault";
import { Recovery } from "./pages/Recovery";


  function App() {

    return (
      <AuthProvider>
        <Routes>
          <Route path="/" element={<HomePage />} />
          <Route path="/login" element={<LoginPage />}></Route>
          <Route path="/register" element={<Register />}></Route>
          <Route path="/recovery" element={<Recovery />}></Route>
          <Route
            path="/vault"
            element={
              <RequireAuth>
                <Vault/>
              </RequireAuth>
            }
          />
        </Routes>
      </AuthProvider>
    );
  }
  export default App;