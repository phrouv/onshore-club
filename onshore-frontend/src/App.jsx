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
import AdminCategories from "./pages/admin/AdminCategories";

function Home() {
  const user = useSelector((s) => s.auth.user);
  return user ? (
    <Navigate to={ROLE_HOME[user.role] || "/login"} replace />
  ) : (
    <Landing />
  );
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
            <Route path="explore" element={<Explore />} />
            <Route path="planning" element={<Planning />} />
            <Route path="credits" element={<Credits />} />
            <Route path="profile" element={<MemberProfile />} />
          </Route>
        </Route>

        <Route element={<ProtectedRoute role="ROLE_PARTNER" />}>
          <Route path="/partner" element={<PartnerShell />}>
            <Route index element={<PartnerDashboard />} />
            <Route path="activities" element={<PartnerActivities />} />
            <Route path="classes" element={<PartnerClasses />} />
            <Route path="bookings" element={<PartnerBookings />} />
            <Route path="earnings" element={<PartnerEarnings />} />
            <Route path="profile" element={<PartnerProfile />} />
          </Route>
        </Route>

        <Route element={<ProtectedRoute role="ROLE_SYSTEM_ADMIN" />}>
          <Route path="/admin" element={<AdminShell />}>
            <Route index element={<AdminDashboard />} />
            <Route path="partners" element={<AdminPartners />} />
            <Route path="members" element={<AdminMembers />} />
            <Route path="bookings" element={<AdminBookings />} />
            <Route path="credits" element={<AdminCredits />} />
            <Route path="payouts" element={<AdminPayouts />} />
            <Route path="rules" element={<AdminRules />} />
            <Route path="categories" element={<AdminCategories />} />
          </Route>
        </Route>

        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
    </>
  );
}
