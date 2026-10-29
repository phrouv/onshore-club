import { useState } from "react";
import { Link, Navigate } from "react-router-dom";
import { useDispatch, useSelector } from "react-redux";
import { ROLE_HOME, clearAuthError, login } from "../../Redux/auth/authSlice";
import { Button } from "../../components/ui/Button";
import { Field, Input } from "../../components/ui/Field";
import { ErrorBanner } from "../../components/ui/Feedback";

export default function Login() {
  const dispatch = useDispatch();
  const { user, loading, error } = useSelector((s) => s.auth);
  const [form, setForm] = useState({ email: "", password: "" });

  if (user) return <Navigate to={ROLE_HOME[user.role] || "/"} replace />;

  const submit = (event) => {
    event.preventDefault();
    dispatch(clearAuthError());
    dispatch(login(form));
  };

  return (
    <div className="mx-auto flex min-h-screen max-w-sm flex-col justify-center gap-4 p-6">
      <h1 className="text-2xl font-extrabold text-primary">Connexion</h1>
      <form onSubmit={submit} className="flex flex-col gap-3">
        <Field label="E-mail">
          <Input type="email" required autoComplete="email" value={form.email} onChange={(e) => setForm({ ...form, email: e.target.value })} />
        </Field>
        <Field label="Mot de passe">
          <Input type="password" required autoComplete="current-password" value={form.password} onChange={(e) => setForm({ ...form, password: e.target.value })} />
        </Field>
        <ErrorBanner message={error} />
        <Button type="submit" disabled={loading}>{loading ? "Connexion..." : "Se connecter"}</Button>
      </form>
      <p className="text-sm text-muted-foreground">
        Pas encore membre ? <Link to="/signup" className="font-semibold text-primary underline">Créer un compte</Link>
      </p>
    </div>
  );
}
