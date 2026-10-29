import { useState } from "react";
import { Link, Navigate, useSearchParams } from "react-router-dom";
import { useDispatch, useSelector } from "react-redux";
import { ROLE_HOME, clearAuthError, signup } from "../../Redux/auth/authSlice";
import { Button } from "../../components/ui/Button";
import { Field, Input } from "../../components/ui/Field";
import { ErrorBanner } from "../../components/ui/Feedback";

export default function Signup() {
  const dispatch = useDispatch();
  const [params] = useSearchParams();
  const role = params.get("role") === "partner" ? "ROLE_PARTNER" : "ROLE_MEMBER";
  const { user, loading, error } = useSelector((s) => s.auth);
  const [form, setForm] = useState({ fullName: "", email: "", phone: "", password: "" });
  const [fieldError, setFieldError] = useState(null);

  if (user) return <Navigate to={ROLE_HOME[user.role] || "/"} replace />;

  const submit = (event) => {
    event.preventDefault();
    if (form.password.length < 8) {
      setFieldError("Le mot de passe doit contenir au moins 8 caractères.");
      return;
    }
    setFieldError(null);
    dispatch(clearAuthError());
    dispatch(signup({ ...form, role }));
  };

  const set = (key) => (e) => setForm({ ...form, [key]: e.target.value });

  return (
    <div className="mx-auto flex min-h-screen max-w-sm flex-col justify-center gap-4 p-6">
      <h1 className="text-2xl font-extrabold text-primary">{role === "ROLE_PARTNER" ? "Devenir partenaire" : "Rejoindre le club"}</h1>
      <form onSubmit={submit} className="flex flex-col gap-3">
        <Field label="Nom complet"><Input required value={form.fullName} onChange={set("fullName")} /></Field>
        <Field label="E-mail"><Input type="email" required autoComplete="email" value={form.email} onChange={set("email")} /></Field>
        <Field label="Téléphone"><Input type="tel" value={form.phone} onChange={set("phone")} /></Field>
        <Field label="Mot de passe" error={fieldError}>
          <Input type="password" required autoComplete="new-password" value={form.password} onChange={set("password")} />
        </Field>
        <ErrorBanner message={error} />
        <Button type="submit" disabled={loading}>{loading ? "Création..." : "Créer mon compte"}</Button>
      </form>
      <p className="text-sm text-muted-foreground">
        Déjà inscrit ? <Link to="/login" className="font-semibold text-primary underline">Se connecter</Link>
      </p>
    </div>
  );
}
