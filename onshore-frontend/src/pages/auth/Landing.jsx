import { Link } from "react-router-dom";
import { Button } from "../../components/ui/Button";

export default function Landing() {
  return (
    <div className="relative flex min-h-screen items-end bg-primary">
      <img src="/assets/onshore-coastal-hero.jpg" alt="" className="absolute inset-0 size-full object-cover opacity-60" />
      <div className="relative mx-auto flex w-full max-w-xl flex-col gap-4 p-6 pb-12 text-primary-foreground">
        <h1 className="text-4xl font-extrabold leading-tight">ON SHORE CLUB</h1>
        <p className="text-lg">Un abonnement, tous les clubs de sport de Seignosse, Hossegor et Capbreton.</p>
        <div className="flex flex-wrap gap-3">
          <Link to="/signup">
            <Button variant="accent" size="lg">Rejoindre le club</Button>
          </Link>
          <Link to="/signup?role=partner">
            <Button variant="outline" size="lg">Devenir partenaire</Button>
          </Link>
          <Link to="/login">
            <Button variant="ghost" size="lg" className="text-primary-foreground hover:bg-white/10">Se connecter</Button>
          </Link>
        </div>
      </div>
    </div>
  );
}
