import { useState } from "react";
import { useSelector } from "react-redux";
import { Button } from "../ui/Button";
import { Field, Input, Textarea } from "../ui/Field";
import { useAction } from "../../hooks/useAction";
import {
  applyAsPartner,
  updatePartner,
} from "../../Redux/partner/partnerSlice";

const EMPTY = {
  name: "",
  address: "",
  city: "",
  description: "",
  phone: "",
  professionalEmail: "",
  website: "",
  instagram: "",
  activityTypes: "",
  desiredSeats: "",
  managerFirstName: "",
  managerLastName: "",
};

const toForm = (partner) =>
  Object.fromEntries(Object.keys(EMPTY).map((k) => [k, partner?.[k] ?? ""]));

/** Partner application (no partner yet) or profile update. */
export function PartnerForm({ partner }) {
  const { run, busy } = useAction();
  const user = useSelector((s) => s.auth.user);
  const [form, setForm] = useState(() => ({
    ...toForm(partner),
    professionalEmail: partner?.professionalEmail ?? user?.email ?? "",
  }));
  const [errors, setErrors] = useState({});

  const set = (key) => (e) => setForm({ ...form, [key]: e.target.value });

  const submit = async (event) => {
    event.preventDefault();
    const next = {};
    for (const key of ["name", "address", "city"]) {
      if (!form[key].trim()) next[key] = "Champ obligatoire";
    }
    setErrors(next);
    if (Object.keys(next).length) return;
    const body = Object.fromEntries(
      Object.entries(form).map(([k, v]) => [k, v === "" ? null : v]),
    );
    if (partner)
      await run(updatePartner({ id: partner.id, body }), "Profil mis à jour");
    else await run(applyAsPartner(body), "Demande envoyée");
  };

  return (
    <form onSubmit={submit} className="grid gap-3 md:grid-cols-2">
      <Field label="Nom du lieu" error={errors.name}>
        <Input value={form.name} onChange={set("name")} />
      </Field>
      <Field label="Ville" error={errors.city}>
        <Input value={form.city} onChange={set("city")} />
      </Field>
      <Field label="Adresse" error={errors.address}>
        <Input value={form.address} onChange={set("address")} />
      </Field>
      <Field label="Téléphone">
        <Input type="tel" value={form.phone} onChange={set("phone")} />
      </Field>
      <Field label="E-mail professionnel">
        <Input
          type="email"
          value={form.professionalEmail}
          onChange={set("professionalEmail")}
        />
      </Field>
      <Field label="Site web">
        <Input value={form.website} onChange={set("website")} />
      </Field>
      <Field label="Instagram">
        <Input value={form.instagram} onChange={set("instagram")} />
      </Field>
      <Field label="Types d'activités proposées">
        <Input value={form.activityTypes} onChange={set("activityTypes")} />
      </Field>
      <Field label="Places souhaitées pour ON SHORE CLUB">
        <Input value={form.desiredSeats} onChange={set("desiredSeats")} />
      </Field>
      <Field label="Prénom du responsable">
        <Input
          value={form.managerFirstName}
          onChange={set("managerFirstName")}
        />
      </Field>
      <Field label="Nom du responsable">
        <Input value={form.managerLastName} onChange={set("managerLastName")} />
      </Field>
      <div className="md:col-span-2">
        <Field label="Description">
          <Textarea value={form.description} onChange={set("description")} />
        </Field>
      </div>
      <Button type="submit" disabled={busy} className="md:col-span-2">
        {partner ? "Enregistrer" : "Envoyer ma demande"}
      </Button>
    </form>
  );
}
