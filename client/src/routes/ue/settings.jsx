import {
    Outlet,
    redirect,
    useNavigate,
    useOutletContext,
} from "react-router-dom";
import ValidatedInput from "../../components/ValidatedInput";
import ValidatedForm from "../../components/ValidatedForm";

export async function action({ request, params }) {
    //TODO: Update section with backend call

    const formData = await request.formData();
    const updates = Object.fromEntries(formData);
    const delay = () => {
        return new Promise((resolve) => {
            setTimeout(() => {
                resolve("Résolu après 2 secondes");
            }, 2000); // 2000 millisecondes = 2 secondes
        });
    };

    await delay();
    console.log(updates);
    return redirect("..");
}

export default function UESettings() {
    const context = useOutletContext();
    const navigate = useNavigate();

    const ue = context.ue;

    const validateField = (name, value) => {
        switch (name) {
            case "name":
                if (value.length < 3)
                    return "Le nom doit contenir au moins 3 caractères.";
                break;
            default:
                return "";
        }
        return ""; // Pas d'erreur
    };

    const onCancel = () => {
        navigate("..");
    }


    return (
        <>
            <ValidatedForm validateField={validateField} onCancel={onCancel}>
                <ValidatedInput
                    name="name"
                    label="Nom"
                    defaultValue={ue.name}
                    margin="normal"
                    fullWidth
                    required
                />
                <ValidatedInput
                    name="description"
                    label="Description"
                    defaultValue={ue.description}
                    multiline
                    minRows={3}
                    margin="normal"
                    fullWidth
                />
            </ValidatedForm>
            <Outlet context={context} />
        </>
    );
}
