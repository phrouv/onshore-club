import { Navigate, Outlet } from "react-router-dom";
import { useSelector } from "react-redux";
import { ROLE_HOME } from "../../Redux/auth/authSlice";

/** Redirects anonymous users to login and users of another persona to their own space. */
export function ProtectedRoute({ role }) {
  const { jwt, user } = useSelector((s) => s.auth);
  if (!jwt || !user) return <Navigate to="/login" replace />;
  if (user.role !== role) return <Navigate to={ROLE_HOME[user.role] || "/login"} replace />;
  return <Outlet />;
}
