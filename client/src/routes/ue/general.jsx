import { Outlet, useLoaderData, useOutletContext } from "react-router-dom";


export default function UEGeneral() {
    const context = useOutletContext();

    const ue = context.ue;

    return (
        <>
            <h1>Général</h1>
            <h2>{ue.name}</h2>
            <p>{ue.description}</p>
            <Outlet context={context} />
        </>
    );
}
