import { useEffect, useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import {
  EmptyState,
  ErrorBanner,
  PageHeader,
} from "../../components/ui/Feedback";
import { Pencil, Power } from "lucide-react";
import { Button } from "../../components/ui/Button";
import { Badge } from "../../components/ui/Badge";
import { Modal } from "../../components/ui/Modal";
import { Field, Input, Textarea } from "../../components/ui/Field";
import { useAction } from "../../hooks/useAction";
import {
  fetchCategories,
  saveCategory,
  setCategoryStatus,
} from "../../Redux/admin/adminSlice";

const EMPTY = { code: "", name: "", description: "", image: "" };
const ACCEPTED_TYPES = ["image/png", "image/jpeg"];
const MAX_IMAGE_BYTES = 2 * 1024 * 1024;

const isDisplayable = (src) => /^(https?:|data:image\/)/.test(src ?? "");

const readAsDataUrl = (file) =>
  new Promise((resolve, reject) => {
    const reader = new FileReader();
    reader.onload = () => resolve(reader.result);
    reader.onerror = () => reject(reader.error);
    reader.readAsDataURL(file);
  });

function CategoryModal({ open, category, onClose }) {
  const { run, busy } = useAction();
  const [form, setForm] = useState(() =>
    category
      ? {
          code: category.code,
          name: category.name,
          description: category.description ?? "",
          image: category.image,
        }
      : EMPTY,
  );
  const [errors, setErrors] = useState({});

  const set = (key) => (e) => setForm({ ...form, [key]: e.target.value });

  const pickImage = async (event) => {
    const file = event.target.files[0];
    if (!file) return;
    if (!ACCEPTED_TYPES.includes(file.type)) {
      setErrors({ ...errors, image: "Format accepté : JPG ou PNG" });
    } else if (file.size > MAX_IMAGE_BYTES) {
      setErrors({ ...errors, image: "Image trop lourde (2 Mo maximum)" });
    } else {
      setErrors({ ...errors, image: undefined });
      setForm({ ...form, image: await readAsDataUrl(file) });
    }
    event.target.value = "";
  };

  const close = () => {
    if (!category) setForm(EMPTY);
    setErrors({});
    onClose();
  };

  const submit = async (event) => {
    event.preventDefault();
    const next = {};
    for (const key of ["code", "name"]) {
      if (!form[key].trim()) next[key] = "Champ obligatoire";
    }
    if (!form.image) next.image = "Image obligatoire";
    if (errors.image) next.image = errors.image;
    setErrors(next);
    if (Object.keys(next).length) return;
    const body = { ...form, description: form.description || null };
    const { ok } = await run(
      saveCategory({ code: category?.code, body }),
      category ? "Catégorie mise à jour" : "Catégorie créée",
    );
    if (ok) close();
  };

  return (
    <Modal
      open={open}
      title={category ? "Modifier la catégorie" : "Nouvelle catégorie"}
      onClose={close}
    >
      <form onSubmit={submit} className="flex flex-col gap-3">
        <Field
          label="Code"
          error={errors.code}
          hint={category ? "Le code ne peut plus être modifié" : undefined}
        >
          <Input
            readOnly={!!category}
            value={form.code}
            onChange={set("code")}
          />
        </Field>
        <Field label="Nom" error={errors.name}>
          <Input value={form.name} onChange={set("name")} />
        </Field>
        <Field
          label="Image"
          error={errors.image}
          hint="JPG ou PNG, 2 Mo maximum"
        >
          <input
            type="file"
            accept="image/png,image/jpeg"
            onChange={pickImage}
            className="text-sm file:mr-3 file:rounded-lg file:border-0 file:bg-muted file:px-3 file:py-2 file:text-sm file:font-medium"
          />
        </Field>
        {isDisplayable(form.image) && (
          <img
            src={form.image}
            alt="Aperçu de la catégorie"
            className="h-32 w-32 rounded-xl border border-border object-cover"
          />
        )}
        <Field label="Description">
          <Textarea
            rows={4}
            value={form.description}
            onChange={set("description")}
          />
        </Field>
        <div className="flex items-center justify-end gap-3">
          <Button type="button" variant="ghost" onClick={close}>
            Annuler
          </Button>
          <Button type="submit" disabled={busy}>
            Enregistrer
          </Button>
        </div>
      </form>
    </Modal>
  );
}

export default function AdminCategories() {
  const dispatch = useDispatch();
  const { categories, error } = useSelector((s) => s.admin);
  const { run, busy } = useAction();
  const [creating, setCreating] = useState(false);
  const [editing, setEditing] = useState(null);

  useEffect(() => {
    dispatch(fetchCategories());
  }, [dispatch]);

  const change = (code, active) =>
    run(
      setCategoryStatus({ code, active }),
      active ? "Catégorie activée" : "Catégorie désactivée",
    );

  return (
    <>
      <PageHeader
        title="Catégories"
        subtitle="Le code est unique et ne peut plus être modifié après la création."
        actions={
          <Button onClick={() => setCreating(true)}>
            + Nouvelle catégorie
          </Button>
        }
      />
      <CategoryModal open={creating} onClose={() => setCreating(false)} />
      {editing && (
        <CategoryModal
          open
          category={editing}
          onClose={() => setEditing(null)}
        />
      )}
      <ErrorBanner message={error} />
      {categories.length === 0 && <EmptyState>Aucune catégorie.</EmptyState>}
      {categories.length > 0 && (
        <div className="overflow-x-auto rounded-2xl border border-border bg-card">
          <table className="w-full text-left text-sm">
            <thead className="border-b border-border text-muted-foreground">
              <tr>
                {["Code", "Nom", "Description", "Image", "Statut", ""].map(
                  (h) => (
                    <th key={h} className="px-3 py-3 font-medium">
                      {h}
                    </th>
                  ),
                )}
              </tr>
            </thead>
            <tbody>
              {categories.map((c) => (
                <tr
                  key={c.code}
                  className="border-b border-border last:border-0"
                >
                  <td className="px-3 py-4 font-medium">{c.code}</td>
                  <td className="px-3 py-4">{c.name}</td>
                  <td className="px-3 py-4 text-muted-foreground">
                    {c.description}
                  </td>
                  <td className="px-3 py-4">
                    {isDisplayable(c.image) && (
                      <img
                        src={c.image}
                        alt={c.name}
                        className="size-12 rounded-lg border border-border object-cover"
                      />
                    )}
                  </td>
                  <td className="px-3 py-4">
                    <Badge tone={c.active ? "info" : "neutral"}>
                      {c.active ? "active" : "inactive"}
                    </Badge>
                  </td>
                  <td className="px-3 py-4">
                    <div className="flex justify-end gap-1">
                      <Button
                        size="sm"
                        variant="ghost"
                        aria-label="Modifier"
                        title="Modifier"
                        onClick={() => setEditing(c)}
                      >
                        <Pencil className="size-4" />
                      </Button>
                      {!c.active && (
                        <Button
                          size="sm"
                          variant="ghost"
                          disabled={busy}
                          onClick={() => change(c.code, true)}
                        >
                          Activer
                        </Button>
                      )}
                      {c.active && (
                        <Button
                          size="sm"
                          variant="ghost"
                          aria-label="Désactiver"
                          title="Désactiver"
                          disabled={busy}
                          onClick={() => change(c.code, false)}
                        >
                          <Power className="size-4" />
                        </Button>
                      )}
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </>
  );
}
