import { Navigate, Route, Routes } from "react-router-dom";
import { useSelector } from "react-redux";
import { ROLE_HOME } from "./Redux/auth/authSlice";
import { Toaster } from "./components/ui/Toaster";
import { ProtectedRoute } from "./components/layout/ProtectedRoute";
import { MemberShell } from "./components/layout/MemberShell";
import { PartnerShell } from "./components/layout/PartnerShell";
import { AdminShell } from "./components/layout/AdminShell";
import Landing from "./pages/auth/Landing";
import Login from "./pages/auth/Login";
import Signup from "./pages/auth/Signup";
import MemberHome from "./pages/member/MemberHome";
import Explore from "./pages/member/Explore";
import Planning from "./pages/member/Planning";
import Credits from "./pages/member/Credits";
import MemberProfile from "./pages/member/MemberProfile";
import PartnerDashboard from "./pages/partner/PartnerDashboard";
import PartnerActivities from "./pages/partner/PartnerActivities";
import PartnerClasses from "./pages/partner/PartnerClasses";
import PartnerBookings from "./pages/partner/PartnerBookings";
import PartnerEarnings from "./pages/partner/PartnerEarnings";
import PartnerProfile from "./pages/partner/PartnerProfile";
import AdminDashboard from "./pages/admin/AdminDashboard";
import AdminPartners from "./pages/admin/AdminPartners";
import AdminMembers from "./pages/admin/AdminMembers";
import AdminBookings from "./pages/admin/AdminBookings";
import AdminCredits from "./pages/admin/AdminCredits";
import AdminPayouts from "./pages/admin/AdminPayouts";
import AdminRules from "./pages/admin/AdminRules";

function Home() {
  const user = useSelector((s) => s.auth.user);
  return user ? <Navigate to={ROLE_HOME[user.role] || "/login"} replace /> : <Landing />;
}

export default function App() {
  return (
    <>
      <Toaster />
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/login" element={<Login />} />
        <Route path="/signup" element={<Signup />} />

        <Route element={<ProtectedRoute role="ROLE_MEMBER" />}>
          <Route path="/member" element={<MemberShell />}>
            <Route index element={<MemberHome />} />
            <Route path="explorer" element={<Explore />} />
            <Route path="planning" element={<Planning />} />
            <Route path="credits" element={<Credits />} />
            <Route path="profil" element={<MemberProfile />} />
          </Route>
        </Route>

        <Route element={<ProtectedRoute role="ROLE_PARTNER" />}>
          <Route path="/partner" element={<PartnerShell />}>
            <Route index element={<PartnerDashboard />} />
            <Route path="activites" element={<PartnerActivities />} />
            <Route path="creneaux" element={<PartnerClasses />} />
            <Route path="reservations" element={<PartnerBookings />} />
            <Route path="gains" element={<PartnerEarnings />} />
            <Route path="profil" element={<PartnerProfile />} />
          </Route>
        </Route>

        <Route element={<ProtectedRoute role="ROLE_SYSTEM_ADMIN" />}>
          <Route path="/admin" element={<AdminShell />}>
            <Route index element={<AdminDashboard />} />
            <Route path="partenaires" element={<AdminPartners />} />
            <Route path="membres" element={<AdminMembers />} />
            <Route path="reservations" element={<AdminBookings />} />
            <Route path="credits" element={<AdminCredits />} />
            <Route path="paiements" element={<AdminPayouts />} />
            <Route path="regles" element={<AdminRules />} />
          </Route>
        </Route>

        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
    </>
  );
}
